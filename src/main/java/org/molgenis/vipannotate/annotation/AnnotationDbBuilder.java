package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.ArrayList;
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
              Iterator<AnnotatedSequenceVariant<CompositeAnnotation>> annotatedIterator =
                  Iterators.map(
                      tsvParser,
                      tsvFeature ->
                          createSeqVarFromTsv(
                              tsvFeature,
                              tsvInputFormat,
                              annotationSpec.annotationSchema().annotationDatasets()));

              createCompositeAnnotatedSequenceVariantDb(
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

  private void createCompositeAnnotatedSequenceVariantDb(
      Iterator<AnnotatedSequenceVariant<CompositeAnnotation>> annotatedIterator,
      List<AnnotationDataset> annotationDatasets,
      BinaryPartitionWriter partitionWriter) {
    List<
            AnnotatedSequenceVariantPartitionWriter<
                SequenceVariant,
                CompositeAnnotation,
                ScalarAnnotation,
                AnnotatedSequenceVariant<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationDatasets.size());

    for (int i = 0; i < annotationDatasets.size(); i++) {
      AnnotationDataset annotationDataset = annotationDatasets.get(i);
      int annotationIndex = i;

      AnnotationDatasetEncoder<ScalarAnnotation> annotationDatasetEncoder =
          createAnnotationDatasetEncoder(annotationDataset);

      partitionWriters.add(
          new AnnotatedSequenceVariantPartitionWriter<>(
              annotationDataset.id(),
              annotationDatasetEncoder,
              partitionWriter,
              variant -> variant.getAnnotation().annotations()[annotationIndex]));
    }

    // TODO check if only needs to be created once
    VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
    MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
        SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
            .createWriter();

    try (CompositeAnnotatedSequenceVariantPartitionWriter<
            SequenceVariant, AnnotatedSequenceVariant<CompositeAnnotation>>
        variantPartitionWriter =
            new CompositeAnnotatedSequenceVariantPartitionWriter<>(partitionWriters)) {

      new AnnotatedSequenceVariantDbWriter<>(
              variantPartitionWriter,
              new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(annotatedIterator);
    }
  }

  private static <T extends Annotation>
      @NonNull AnnotationDatasetEncoder<T> createAnnotationDatasetEncoder(
          AnnotationDataset annotationDataset) {
    AnnotationEncoderTmp<T> annotationEncoder =
        createEncoder(annotationDataset.annotationValue(), false);

    return new AnnotationDatasetEncoder<>() {

      @Override
      public long getEncodedSizeInBytes(int annotationCount) {
        return Math.multiplyExact(annotationCount, annotationEncoder.getEncodedSizeInBytes());
      }

      @Override
      public void encode(
          SizedIterator<T> annotationIt, int maxAnnotations, MemoryBuffer memBuffer) {
        // FIXME deal with -1 index
        annotationIt.forEachRemaining(value -> annotationEncoder.encodeInto(value, memBuffer, -1));
      }
    };
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

    // create value writer
    // FIXME use storage and logical type
    ValueWriter valueWriter = createValueWriter(storageType, writeAtIndex);

    if (encoding == null) {
      if (logicalType.nullable()) {
        // FIXME support  nullable logicalType encoding when encoding is null
        throw new UnsupportedOperationException();
      }
      return switch (storageType.scalarType()) {
        case I8, I16, I32, U8, U16 ->
            (AnnotationEncoderTmp<T>)
                new AnnotationEncoderTmp<ScalarAnnotation.IntAnnotation>() {
                  @Override
                  public void initialize(MemoryBuffer memoryBuffer) {
                    // FIXME implement initialize(MemoryBuffer memBuffer)
                    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
                  }

                  @Override
                  public void encodeInto(
                      ScalarAnnotation.IntAnnotation annotation,
                      MemoryBuffer memBuffer,
                      int index) {
                    valueWriter.write(annotation.getValue(), memBuffer, index);
                  }

                  @Override
                  public long getEncodedSizeInBytes() {
                    return valueWriter.getValueSizeInBytes();
                  }
                };
        case I64, U32, U64, F32, F64 -> {
          // FIXME support null encoding for U64,F32,F64
          throw new UnsupportedOperationException();
        }
      };
    }

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

        yield (AnnotationEncoderTmp<T>)
            new QuantizedAnnotationEncoder(quantizer, valueWriter, quantizedEncoding.nullCode());
      }
    };
  }

  private static ValueWriter createValueWriter(StorageType storageType, boolean writeAtIndex) {
    return switch (storageType.scalarType()) {
      case I8, U8 ->
          new ValueWriter(
              writeAtIndex
                  ? (int value, MemoryBuffer memoryBuffer, int index) ->
                      memoryBuffer.setByteAtIndexUnchecked(index, (byte) value)
                  : (int value, MemoryBuffer memoryBuffer, int _) ->
                      memoryBuffer.putByteUnchecked((byte) value),
              Byte.BYTES);
      case I16, U16 ->
          new ValueWriter(
              writeAtIndex
                  ? (int value, MemoryBuffer memoryBuffer, int index) ->
                      memoryBuffer.setShortAtIndexUnchecked(index, (short) value)
                  : (int value, MemoryBuffer memoryBuffer, int _) ->
                      memoryBuffer.putShortUnchecked((short) value),
              Short.BYTES);
      case I32, U32 ->
          new ValueWriter(
              writeAtIndex
                  ? (int value, MemoryBuffer memoryBuffer, int index) ->
                      memoryBuffer.setIntAtIndexUnchecked(index, value)
                  : (int value, MemoryBuffer memoryBuffer, int _) ->
                      memoryBuffer.putIntUnchecked(value),
              Integer.BYTES);
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
    int[] idxAnnotations = tsvInputFormat.annotations();
    if (idxAnnotations.length != 1) {
      throw new UnsupportedOperationException("not implemented"); // FIXME
    }
    int idxAnnotation = idxAnnotations[0];

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
      String[] tsvFeature,
      TsvInputFormat tsvInputFormat,
      List<AnnotationDataset> annotationDatasets) {
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

    T annotation = createAnnotationFromTsvFeature(tsvFeature, tsvInputFormat, annotationDatasets);
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
      String[] tsvFeature,
      TsvInputFormat tsvInputFormat,
      List<AnnotationDataset> annotationDatasets) {
    int[] idxAnnotations = tsvInputFormat.annotations();
    if (idxAnnotations.length == 0) {
      throw new IllegalArgumentException();
      //    }
      //    else if (idxAnnotations.length == 1) {
      //      int idxAnnotation = idxAnnotations[0];
      //      return (T) new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation]));
    } else {
      ScalarAnnotation[] scalarAnnotations = new ScalarAnnotation[idxAnnotations.length];
      for (int i = 0; i < idxAnnotations.length; i++) {
        int idxAnnotation = idxAnnotations[i];
        AnnotationDataset annotationDataset = annotationDatasets.get(i);
        scalarAnnotations[i] =
            switch (annotationDataset.annotationValue().logicalType().scalarType()) {
              case I8, I16, I32, U8, U16 ->
                  new ScalarAnnotation.IntAnnotation(Integer.parseInt(tsvFeature[idxAnnotation]));
              case F32, F64 -> new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation]));
              // FIXME createAnnotationFromTsvFeature support I64,U32,U64
              case I64, U32, U64 -> throw new UnsupportedOperationException();
            };
        // FIXME remove hardcoded hack
        if (idxAnnotation == 15) {
          scalarAnnotations[i] =
              new ScalarAnnotation.IntAnnotation(Integer.parseInt(tsvFeature[idxAnnotation]));
        } else {
          scalarAnnotations[i] =
              new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation]));
        }
      }
      return (T) new CompositeAnnotation(scalarAnnotations);
    }
  }
}
