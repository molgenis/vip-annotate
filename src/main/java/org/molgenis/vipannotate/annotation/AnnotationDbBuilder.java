package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.DoubleAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableDoubleAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.annotation.spec.AnnotationDataset;
import org.molgenis.vipannotate.format.bed.BedFeature;
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
      AnnotationSpec annotationSpec, Input input, BinaryPartitionWriter partitionWriter) {
    InputFormat inputFormat = annotationSpec.inputFormat();
    AnnotationSchema annotationSchema = annotationSpec.annotationSchema();
    switch (inputFormat) {
      case BedInputFormat bedInputFormat ->
          createFromBed(bedInputFormat, annotationSchema, input, partitionWriter);
      case TsvInputFormat tsvInputFormat ->
          createFromTsv(tsvInputFormat, annotationSchema, input, partitionWriter);
      case VcfInputFormat vcfInputFormat ->
          createFromVcf(vcfInputFormat, annotationSchema, input, partitionWriter);
    }
  }

  private void createFromBed(
      BedInputFormat bedInputFormat,
      AnnotationSchema annotationSchema,
      Input bedInput,
      BinaryPartitionWriter partitionWriter) {
    // FIXME implement createFromBed
    throw new UnsupportedOperationException();
    //    try (BedParser bedParser = BedParserFactory.create(bedInput)) {
    //      Iterator<AnnotatedInterval<Position, ScalarAnnotation>> annotatedPosIterator =
    //          createAnnotatedPosIteratorFromBed(bedParser, bedInputFormat);
    //
    //      if (annotationSchema.annotationType() != AnnotationType.POSITION) {
    //        throw new UnsupportedOperationException(); // FIXME clear error msg
    //      }
    //
    //      createAnnotatedIntervalDb(
    //          annotatedPosIterator, annotationSchema.annotationDatasets(), partitionWriter);
    //    }
  }

  private void createFromTsv(
      TsvInputFormat tsvInputFormat,
      AnnotationSchema annotationSchema,
      Input tsvInput,
      BinaryPartitionWriter partitionWriter) {
    try (TsvParser tsvParser = TsvParserFactory.create(tsvInput)) {
      switch (annotationSchema.annotationType()) {
        case SEQUENCE_VARIANT -> {
          Iterator<AnnotatedSequenceVariant<CompositeAnnotation>> annotatedIterator =
              Iterators.map(
                  tsvParser,
                  tsvFeature ->
                      createSeqVarFromTsv(
                          tsvFeature, tsvInputFormat, annotationSchema.annotationDatasets()));

          createCompositeAnnotatedSequenceVariantDb(
              annotatedIterator, annotationSchema.annotationDatasets(), partitionWriter);
        }
        case POSITION -> {
          Iterator<AnnotatedPosition<CompositeAnnotation>> annotatedIterator =
              Iterators.map(
                  tsvParser,
                  tsvFeature ->
                      createPosFromTsv(
                          tsvFeature, tsvInputFormat, annotationSchema.annotationDatasets()));

          createAnnotatedIntervalDb(
              annotatedIterator, annotationSchema.annotationDatasets(), partitionWriter);
        }
      }
    }
  }

  private void createFromVcf(
      VcfInputFormat vcfInputFormat,
      AnnotationSchema annotationSchema,
      Input vcfInput,
      BinaryPartitionWriter partitionWriter) {
    // FIXME implement vcf
    throw new UnsupportedOperationException();
  }

  private Iterator<AnnotatedInterval<Position, ScalarAnnotation>> createAnnotatedPosIteratorFromBed(
      Iterator<BedFeature> bedFeatureIterator, BedInputFormat bedInputFormat) {
    // FIXME bed support
    throw new UnsupportedOperationException();
    //    return Iterators.flatMap(
    //        Iterators.map(bedFeatureIterator, bedFeature -> create(bedFeature,
    // bedInputFormat.from())),
    //        GenomicIterators.iteratePositions());
  }

  private void createCompositeAnnotatedSequenceVariantDb(
      Iterator<AnnotatedSequenceVariant<CompositeAnnotation>> annotatedIterator,
      Map<String, AnnotationDataset> annotationDatasets,
      BinaryPartitionWriter partitionWriter) {
    List<
            AnnotatedIntervalPartitionWriter<
                SequenceVariant,
                CompositeAnnotation,
                AnnotatedSequenceVariant<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationDatasets.size());

    int i = 0;
    for (Map.Entry<String, AnnotationDataset> entry : annotationDatasets.entrySet()) {
      String annotationDatasetId = entry.getKey();
      AnnotationDataset value = entry.getValue();
      AnnotationDatasetEncoder<Annotation> annotationDatasetEncoder =
          createAnnotationDatasetEncoder(value);

      int annotationIndex = i++;
      partitionWriters.add(
          new AnnotatedSequenceVariantPartitionWriter<>(
              annotationDatasetId,
              annotationDatasetEncoder,
              partitionWriter,
              variant -> variant.getAnnotation().annotations()[annotationIndex]));
    }

    // TODO check if only needs to be created once
    VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
    MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
        SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
            .createWriter();

    try (CompositeAnnotatedIntervalPartitionWriter<
            SequenceVariant, AnnotatedSequenceVariant<CompositeAnnotation>>
        variantPartitionWriter =
            new CompositeAnnotatedIntervalPartitionWriter<>(partitionWriters)) {

      new AnnotatedSequenceVariantDbWriter<>(
              variantPartitionWriter,
              new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(annotatedIterator);
    }
  }

  private static <T extends Annotation> AnnotationDatasetEncoder<T> createAnnotationDatasetEncoder(
      AnnotationDataset annotationDataset) {
    LogicalType logicalType = annotationDataset.logicalType();
    return (AnnotationDatasetEncoder<T>)
        switch (logicalType) {
          case EnumLogicalType enumLogicalType -> new EnumAnnotationDatasetEncoder(enumLogicalType);
          case EnumSetLogicalType enumSetLogicalType ->
              new EnumSetAnnotationDatasetEncoder(enumSetLogicalType);
          case ScalarLogicalType scalarLogicalType ->
              createScalarAnnotationDatasetEncoder(scalarLogicalType, annotationDataset);
        };
  }

  private static <T extends Annotation>
      AnnotationDatasetEncoder<T> createScalarAnnotationDatasetEncoder(
          ScalarLogicalType scalarLogicalType, AnnotationDataset annotationDataset) {
    AnnotationEncoder<T> annotationEncoder =
        createScalarEncoder(
            scalarLogicalType,
            annotationDataset.encoding(),
            annotationDataset.storageType(),
            false);

    // TODO move to ScalarAnnotationDatasetEncoder
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
      Iterator<AnnotatedPosition<CompositeAnnotation>> annotatedPosIterator,
      Map<String, AnnotationDataset> annotationDatasets,
      BinaryPartitionWriter partitionWriter) {
    List<
            AnnotatedIntervalPartitionWriter<
                Position, CompositeAnnotation, AnnotatedPosition<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationDatasets.size());

    int i = 0;
    for (Map.Entry<String, AnnotationDataset> entry : annotationDatasets.entrySet()) {
      String annotationDatasetId = entry.getKey();
      AnnotationDataset annotationDataset = entry.getValue();
      IndexedAnnotationEncoder<Annotation> annotationEncoder =
          createIndexedEncoder(annotationDataset);

      int annotationIndex = i++;
      partitionWriters.add(
          new AnnotatedPositionPartitionWriter<>(
              annotationDatasetId,
              new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder),
              partitionWriter,
              variant -> variant.getAnnotation().annotations()[annotationIndex]));
    }

    try (CompositeAnnotatedIntervalPartitionWriter<Position, AnnotatedPosition<CompositeAnnotation>>
        posPartitionWriter = new CompositeAnnotatedIntervalPartitionWriter<>(partitionWriters)) {
      AnnotatedIntervalDbWriter<
              Position, CompositeAnnotation, AnnotatedPosition<CompositeAnnotation>>
          annotationDbWriter = new AnnotatedIntervalDbWriter<>(posPartitionWriter);

      annotationDbWriter.write(annotatedPosIterator);
    }
  }

  private static IndexedAnnotationEncoder<Annotation> createIndexedEncoder(
      AnnotationDataset annotationDataset) {
    AnnotationEncoder<Annotation> annotationEncoder = createEncoder(annotationDataset, true);
    return new IndexedAnnotationEncoder<>(annotationEncoder);
  }

  private static <T extends Annotation> AnnotationEncoder<T> createEncoder(
      AnnotationDataset annotationDataset, boolean writeAtIndex) {
    StorageType storageType = annotationDataset.storageType();
    return switch (annotationDataset.logicalType()) {
      case EnumLogicalType enumLogicalType ->
          createEnumEncoder(
              enumLogicalType, annotationDataset.encoding(), storageType, writeAtIndex);
      case EnumSetLogicalType enumSetLogicalType ->
          createEnumSetEncoder(
              enumSetLogicalType, annotationDataset.encoding(), storageType, writeAtIndex);
      case ScalarLogicalType scalarLogicalType ->
          createScalarEncoder(
              scalarLogicalType, annotationDataset.encoding(), storageType, writeAtIndex);
    };
  }

  private static <T extends Annotation> AnnotationEncoder<T> createEnumEncoder(
      EnumLogicalType logicalType,
      Encoding encoding,
      StorageType storageType,
      boolean writeAtIndex) {
    // FIXME implement createEnumEncoder
    throw new UnsupportedOperationException();
  }

  private static <T extends Annotation> AnnotationEncoder<T> createEnumSetEncoder(
      EnumSetLogicalType logicalType,
      Encoding encoding,
      StorageType storageType,
      boolean writeAtIndex) {
    // in GnomAdAnnotationDatasetEncoder we pack multiple enum set annotations in one byte
    // do something like public sealed interface StorageType permits ScalarStorageType,
    // BitSetStorageType {}?

    // FIXME implement createEnumSetEncoder
    throw new UnsupportedOperationException();
  }

  private static <T extends Annotation> AnnotationEncoder<T> createScalarEncoder(
      ScalarLogicalType logicalType,
      Encoding encoding,
      StorageType storageType,
      boolean writeAtIndex) {

    return (AnnotationEncoder<T>)
        new ScalarAnnotationEncoderFactory(new ValueWriterFactory())
            .create(logicalType, encoding, storageType, writeAtIndex);
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

  private <T extends Annotation> AnnotatedPosition<T> createPosFromTsv(
      String[] tsvFeature,
      TsvInputFormat tsvInputFormat,
      Map<String, AnnotationDataset> annotationDatasets) {
    int idxContig = tsvInputFormat.contig();
    int idxStart = tsvInputFormat.start();

    Contig contig = new Contig(tsvFeature[idxContig], 9); // FIXME
    int start = Integer.parseInt(tsvFeature[idxStart]);
    switch (tsvInputFormat.coordinateSystem()) {
      case ZERO_BASED -> start++;
      case ONE_BASED -> {}
    }

    T annotation = createAnnotationFromTsvFeature(tsvFeature, tsvInputFormat, annotationDatasets);
    return new AnnotatedPosition<>(new Position(contig, start), annotation);
  }

  private <T extends Annotation> AnnotatedSequenceVariant<T> createSeqVarFromTsv(
      String[] tsvFeature,
      TsvInputFormat tsvInputFormat,
      Map<String, AnnotationDataset> annotationDatasets) {
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
      Map<String, AnnotationDataset> annotationDatasets) {
    Map<String, Integer> idxAnnotations = tsvInputFormat.annotations();
    if (idxAnnotations.isEmpty()) {
      throw new IllegalArgumentException();
      //    }
      //    else if (idxAnnotations.length == 1) {
      //      int idxAnnotation = idxAnnotations[0];
      //      return (T) new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation]));
    } else {
      List<Annotation> annotations = new ArrayList<>(annotationDatasets.size());
      annotationDatasets.forEach(
          (annotationId, annotationDataset) -> {
            Integer idxAnnotation = idxAnnotations.get(annotationId);
            if (idxAnnotation == null) {
              throw new IllegalArgumentException(
                  "'schema.annotation_datasets.%s' not defined in 'input.annotations'"
                      .formatted(annotationId));
            }
            annotations.add(
                createAnnotationFromTsvValue(tsvFeature[idxAnnotation], annotationDataset));
          });
      return (T) new CompositeAnnotation(annotations.toArray(new Annotation[0]));
    }
  }

  private <T extends Annotation> T createAnnotationFromTsvValue(
      String tsvValue, AnnotationDataset annotationDataset) {
    return (T)
        switch (annotationDataset.logicalType()) {
          case EnumLogicalType enumLogicalType ->
              createAnnotationFromTsvValue(tsvValue, enumLogicalType);
          case EnumSetLogicalType enumSetLogicalType ->
              createAnnotationFromTsvValue(tsvValue, enumSetLogicalType);
          case ScalarLogicalType scalarLogicalType ->
              createAnnotationFromTsvValue(tsvValue, scalarLogicalType);
        };
  }

  private Annotation createAnnotationFromTsvValue(String tsvValue, ScalarLogicalType logicalType) {
    return switch (logicalType.scalarType()) {
      case I8, I16, I32, U8, U16 ->
          logicalType.nullable()
              ? (tsvValue.isEmpty()
                  ? new NullableIntAnnotation()
                  : new NullableIntAnnotation(Integer.parseInt(tsvValue)))
              : new IntAnnotation(Integer.parseInt(tsvValue));
      case F32, F64 ->
          logicalType.nullable()
              ? (tsvValue.isEmpty()
                  ? new NullableDoubleAnnotation()
                  : new NullableDoubleAnnotation(Double.parseDouble(tsvValue)))
              : new DoubleAnnotation(Double.parseDouble(tsvValue));
      // FIXME createAnnotationFromTsvFeature support I64,U32,U64
      case I64, U32, U64 -> throw new UnsupportedOperationException();
    };
  }

  private Annotation createAnnotationFromTsvValue(
      String tsvValue, EnumLogicalType enumLogicalType) {
    return new StringAnnotation(!tsvValue.isEmpty() ? tsvValue : null);
  }

  private Annotation createAnnotationFromTsvValue(String tsvValue, EnumSetLogicalType logicalType) {
    String[] tokens = !tsvValue.isEmpty() ? tsvValue.split(",", -1) : new String[0];
    // FIXME map empty token to null
    return new StringListAnnotation(tokens);
  }
}
