package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.DoubleAnnotation;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.annotation.spec.AnnotationDataset;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedParser;
import org.molgenis.vipannotate.format.bed.BedParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvParser;
import org.molgenis.vipannotate.format.tsv.TsvParserFactory;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.*;

public class AnnotationDbBuilder {
  public AnnotationDbBuilder() {}

  public void create(
      AnnotationSpec annotationSpec,
      Path resourceDir,
      //      Input input,
      //      @Nullable List<Region> regions,
      //      FastaIndex fastaIndex,
      BinaryPartitionWriter partitionWriter) {
    InputFormat inputFormat = annotationSpec.inputFormat();
    switch (inputFormat.type()) {
      case BED -> {
        BedInputFormat bedInputFormat = (BedInputFormat) inputFormat;
        Input bedInput = new Input(resourceDir.resolve(bedInputFormat.file()));
        try (BedParser bedParser = BedParserFactory.create(bedInput)) {
          Iterator<AnnotatedInterval<Position, ScalarAnnotation>> annotatedPosIterator =
              createAnnotatedPosIteratorFromBed(bedParser, bedInputFormat);

          if (annotationSpec.annotationSchema().annotationType() != AnnotationType.POSITION) {
            throw new UnsupportedOperationException(); // FIXME clear error msg
          }

          createAnnotatedIntervalDb(
              annotatedPosIterator,
              annotationSpec.annotationSchema().annotationDatasets(),
              partitionWriter);
        }
      }
      case TSV -> {
        TsvInputFormat tsvInputFormat = (TsvInputFormat) inputFormat;
        Input tsvInput = new Input(resourceDir.resolve(tsvInputFormat.file()));
        try (TsvParser tsvParser = TsvParserFactory.create(tsvInput)) {
          switch (annotationSpec.annotationSchema().annotationType()) {
            case SEQUENCE_VARIANT -> {
              Iterator<AnnotatedSequenceVariant<Annotation>> annotatedIterator =
                  Iterators.map(
                      tsvParser, tsvFeature -> createSeqVarFromTsv(tsvFeature, tsvInputFormat));

              createAnnotatedSequenceVariantDb(
                  annotatedIterator,
                  annotationSpec.annotationSchema().annotationDatasets(),
                  partitionWriter);
            }
            case POSITION -> {
              Iterator<AnnotatedInterval<Position, ScalarAnnotation>> annotatedIterator =
                  Iterators.map(
                      tsvParser, tsvFeature -> createPosFromTsv(tsvFeature, tsvInputFormat));

              createAnnotatedIntervalDb(
                  annotatedIterator,
                  annotationSpec.annotationSchema().annotationDatasets(),
                  partitionWriter);
            }
          }
        }
      }
      case VCF -> throw new RuntimeException("implement vcf"); // FIXME
    }
  }

  private @NonNull Iterator<AnnotatedInterval<Position, ScalarAnnotation>>
      createAnnotatedPosIteratorFromBed(
          Iterator<BedFeature> bedFeatureIterator, BedInputFormat bedInputFormat) {
    return Iterators.flatMap(
        Iterators.map(bedFeatureIterator, bedFeature -> create(bedFeature, bedInputFormat.from())),
        GenomicIterators.iteratePositions());
  }

  private <T extends Annotation> void createAnnotatedSequenceVariantDb(
      Iterator<AnnotatedSequenceVariant<T>> annotatedIterator,
      List<AnnotationDataset> annotationDatasets,
      BinaryPartitionWriter partitionWriter) {

    // get annotation dataset definition
    if (annotationDatasets.size() != 1) {
      throw new IllegalArgumentException(); // FIXME handle other sizes
    }
    AnnotationDataset annotationDataset = annotationDatasets.getFirst();

    AnnotationEncoderTmp<T> annotationEncoder =
        createEncoder(annotationDataset.annotationValue(), false);

    AnnotationDatasetEncoder<T> annotationDatasetEncoder =
        (annotationIt, _, memBuffer) ->
            annotationIt.forEachRemaining(
                value -> {
                  annotationEncoder.encodeInto(value, memBuffer, -1); // FIXME
                });

    // TODO check if only needs to be created once
    VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
    MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
        SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
            .createWriter();

    try (AnnotatedSequenceVariantPartitionWriter<SequenceVariant, T, AnnotatedSequenceVariant<T>>
        variantPartitionWriter =
            new AnnotatedSequenceVariantPartitionWriter<>(
                annotationDataset.id(), annotationDatasetEncoder, partitionWriter)) {
      new AnnotatedSequenceVariantDbWriter<>(
              variantPartitionWriter,
              new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(annotatedIterator);
    }
  }

  private void createAnnotatedIntervalDb(
      Iterator<AnnotatedInterval<Position, ScalarAnnotation>> annotatedPosIterator,
      List<AnnotationDataset> annotationDatasets,
      BinaryPartitionWriter partitionWriter) {

    // get annotation dataset definition
    if (annotationDatasets.size() != 1) {
      throw new IllegalArgumentException(); // FIXME handle other sizes
    }
    AnnotationDataset annotationDataset = annotationDatasets.getFirst();

    IndexedAnnotationEncoder<ScalarAnnotation> annotationEncoder =
        createIndexedEncoder(annotationDataset.annotationValue());

    // annotation dataset writer
    try (AnnotatedPositionPartitionWriter<
            Position, ScalarAnnotation, AnnotatedInterval<Position, ScalarAnnotation>>
        posPartitionWriter =
            new AnnotatedPositionPartitionWriter<>(
                annotationDataset.id(),
                new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder),
                partitionWriter)) {

      AnnotatedIntervalDbWriter<
              Position, ScalarAnnotation, AnnotatedInterval<Position, ScalarAnnotation>>
          annotationDbWriter = new AnnotatedIntervalDbWriter<>(posPartitionWriter);

      annotationDbWriter.write(annotatedPosIterator);
    }
  }

  private static IndexedAnnotationEncoder<ScalarAnnotation> createIndexedEncoder(
      AnnotationValue annotationValue) {
    AnnotationEncoderTmp<ScalarAnnotation> annotationEncoder = createEncoder(annotationValue, true);

    return new IndexedAnnotationEncoder<>() {
      @Override
      public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
        annotationEncoder.initialize(memoryBuffer);
        //        short encodedNullScore = doubleCodec.encodeDoubleAsShort(null, doubleInterval);
        //        // TODO use .fill(..)
        //        for (int i = indexRange.start(), indexEnd = indexRange.end(); i <= indexEnd; i++)
        // {
        //          memoryBuffer.setShortAtIndex(i, encodedNullScore);
        //        }
        System.err.println(
            "FIXME implement clear(IndexRange indexRange, MemoryBuffer memoryBuffer)");
      }

      @Override
      public int getAnnotationSizeInBytes() {
        return Short.BYTES;
      }

      @Override
      public void encodeInto(
          IndexedAnnotation<ScalarAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
        annotationEncoder.encodeInto(
            indexedAnnotation.getFeatureAnnotation(), memoryBuffer, indexedAnnotation.getIndex());
      }
    };
  }

  private static <T extends Annotation> AnnotationEncoderTmp<T> createEncoder(
      AnnotationValue annotationValue, boolean writeAtIndex) {
    StorageType storageType = annotationValue.storageType();
    LogicalType logicalType = annotationValue.logicalType();
    Encoding encoding = annotationValue.encoding();

    return switch (encoding.encodingType()) {
      case QUANTIZED -> {
        // create quantizer
        QuantizedEncoding quantizedEncoding = (QuantizedEncoding) encoding;
        QuantizedEncoding.Range range = quantizedEncoding.range();
        QuantizedEncoding.Levels levels = quantizedEncoding.levels();
        Quantizer quantizer =
            new Quantizer(
                new DoubleInterval(range.min(), range.max()),
                new IntInterval(levels.min(), levels.max()));

        // create value writer
        // FIXME use storage and logical type
        WriteValueFunction writeValueFunction = createWriteValueFunction(storageType, writeAtIndex);

        yield (AnnotationEncoderTmp<T>)
            new QuantizedAnnotationEncoder(
                quantizer, writeValueFunction, quantizedEncoding.nullCode());
      }
    };
  }

  private static WriteValueFunction createWriteValueFunction(
      StorageType storageType, boolean writeAtIndex) {
    return switch (storageType.scalarType()) {
      case I8, U8 ->
          writeAtIndex
              ? (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, (byte) value)
              : (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked((byte) value);
      case I16, U16 ->
          writeAtIndex
              ? (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(index, (short) value)
              : (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked((short) value);
      case I32, U32 ->
          writeAtIndex
              ? (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(index, value)
              : (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putIntUnchecked(value);
      default ->
          throw new UnsupportedOperationException(
              "Unsupported storage type: %s"
                  .formatted(storageType)); // FIXME support I64, U64, F32 and F64
    };
  }

  // TODO improve performance by reusing annotated interval
  // TODO improve performance by reusing contig
  private AnnotatedInterval<Interval, ScalarAnnotation> create(
      BedFeature bedFeature, BedField from) {
    Contig contig = new Contig(bedFeature.getChrom().get().toString(), 9); // FIXME hardcoded
    int start = bedFeature.getChromStart().get();
    int end = bedFeature.getChromEnd().get();
    if (end - start == 0) {
      // source: https://samtools.github.io/hts-specs/BEDv1.pdf
      // If chromEnd is equal to chromStart, this indicates a feature between chromStart and the
      // preceding base, such as an insertion.
      throw new UnsupportedOperationException();
    }

    Interval interval;
    if (end - start == 1) {
      // 1-based inclusive
      interval = new Position(contig, start + 1);
    } else {
      // [1-based inclusive, 1-based inclusive]
      interval = new Interval(contig, start + 1, end);
    }
    if (from.getColIndex() == 3) {
      // FIXME get value type from spec, support other things then double
      return new AnnotatedInterval<>(
          interval,
          new DoubleAnnotation(Double.parseDouble(bedFeature.getName().get().toString())));
    }
    throw new RuntimeException("not implemented"); // FIXME support data in other cols e.g. score
  }

  private AnnotatedPosition<ScalarAnnotation> createPosFromTsv(
      String[] tsvFeature, TsvInputFormat tsvInputFormat) {
    int idxContig = tsvInputFormat.contig();
    int idxStart = tsvInputFormat.start();
    int idxAnnotation = tsvInputFormat.annotation();

    Contig contig = new Contig(tsvFeature[idxContig], 9); // FIXME
    int start = Integer.parseInt(tsvFeature[idxStart]);
    switch (tsvInputFormat.coordinateSystem()) {
      case ZERO_BASED -> start++;
      case ONE_BASED -> {}
    }
    return new AnnotatedPosition<>(
        new Position(contig, start),
        new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation])));
  }

  private <T extends Annotation> AnnotatedSequenceVariant<T> createSeqVarFromTsv(
      String[] tsvFeature, TsvInputFormat tsvInputFormat) {
    int idxContig = tsvInputFormat.contig();
    int idxStart = tsvInputFormat.start();
    int idxRef = tsvInputFormat.ref();
    Integer idxAlt = tsvInputFormat.alt();
    if (idxAlt == null) {
      throw new IllegalArgumentException();
    }

    Contig contig = new Contig(tsvFeature[idxContig], 9); // FIXME
    int start = Integer.parseInt(tsvFeature[idxStart]);
    int refLen = tsvFeature[idxRef].length();
    AltAllele alt = new AltAllele(tsvFeature[idxAlt]);
    switch (tsvInputFormat.coordinateSystem()) {
      case ZERO_BASED -> start++;
      case ONE_BASED -> {}
    }

    T annotation = createAnnotationFromTsvFeature(tsvFeature, tsvInputFormat);
    return new AnnotatedSequenceVariant<>(
        new SequenceVariant(
            contig,
            start,
            start + refLen - 1,
            alt,
            SequenceVariantTypeDetector.determineType(refLen, alt)),
        annotation);
  }

  private <T extends Annotation> T createAnnotationFromTsvFeature(
      String[] tsvFeature, TsvInputFormat tsvInputFormat) {
    int idxAnnotation = tsvInputFormat.annotation();
    return (T) new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation]));
  }
}
