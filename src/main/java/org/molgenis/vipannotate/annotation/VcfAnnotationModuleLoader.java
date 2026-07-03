package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.annotation.spec.AnnotationDataset;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.NumberCollections;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VcfAnnotationModuleLoader {
  private final PartitionedVdbArchiveReaderFactory archiveReaderFactory;
  private final AnnotationSpecLoader schemaLoader;

  public VcfAnnotationModule load(Path annotationDbPath) {
    PartitionedVdbArchiveReader archiveReader = archiveReaderFactory.create(annotationDbPath);

    AnnotationSpec annotationSpec = schemaLoader.load(archiveReader);

    VcfHeaderAnnotator headerAnnotator = createHeaderAnnotator(annotationSpec);
    VcfRecordAnnotator recordAnnotator = createRecordAnnotator(annotationSpec, archiveReader);
    return new VcfAnnotationModule(headerAnnotator, recordAnnotator);
  }

  private VcfHeaderAnnotator createHeaderAnnotator(AnnotationSpec annotationSpec) {
    VcfOutputFormat output = (VcfOutputFormat) annotationSpec.outputFormat();
    return new InfoVcfHeaderAnnotator(
        output.infoId(),
        output.infoNumber(),
        output.infoType(),
        output.infoDescription(),
        AppMetadata.getName(),
        "%s+db%s".formatted(AppMetadata.getVersion(), output.infoVersion()));
  }

  private AnnotationDatasetReader<ScalarAnnotation> createScalarAnnotationDatasetReader(
      AnnotationDataset annotationDataset, PartitionedVdbArchiveReader archiveReader) {
    AnnotationDecoder<ScalarAnnotation> annotationDecoder =
        createAnnotationDecoder(annotationDataset.annotationValue());
    AnnotationBlobReader blobReader =
        new AnnotationBlobReader(annotationDataset.id(), archiveReader);
    return new ScalarAnnotationDatasetReader(annotationDecoder, blobReader);
  }

  private AnnotationDecoder<ScalarAnnotation> createAnnotationDecoder(
      AnnotationValue annotationValue) {
    return switch (annotationValue.encoding().encodingType()) {
      case QUANTIZED ->
          createQuantizedAnnotationDecoder(
              annotationValue.storageType(),
              annotationValue.logicalType(),
              (QuantizedEncoding) annotationValue.encoding());
    };
  }

  private static QuantizedAnnotationDecoder createQuantizedAnnotationDecoder(
      StorageType storageType, LogicalType logicalType, QuantizedEncoding encoding) {
    Quantizer quantizer = createQuantizer(logicalType, encoding);

    ReadValueFunction readValueFunction =
        switch (storageType.scalarType()) {
          case I8 -> MemoryBuffer::getByteAtIndex;
          case I16 -> MemoryBuffer::getShortAtIndex;
          case U8 -> MemoryBuffer::getUnsignedByteAtIndex;
          case U16 -> MemoryBuffer::getUnsignedShortAtIndex;
          default -> throw new IllegalArgumentException();
        };
    return new QuantizedAnnotationDecoder(quantizer, readValueFunction, encoding.nullCode());
  }

  private static Quantizer createQuantizer(LogicalType logicalType, QuantizedEncoding encoding) {
    if (logicalType.scalarType() != ScalarType.F64) {
      throw new IllegalArgumentException();
    }
    if (logicalType.nullable() && encoding.nullCode() == null) {
      throw new IllegalArgumentException();
    }
    if (!logicalType.nullable() && encoding.nullCode() != null) {
      throw new IllegalArgumentException();
    }
    QuantizedEncoding.Range range = encoding.range();
    QuantizedEncoding.Levels levels = encoding.levels();
    return new Quantizer(
        new DoubleInterval(range.min(), range.max()), new IntInterval(levels.min(), levels.max()));
  }

  private AnnotationDatasetReader<?> createCompositeAnnotationDatasetReader(
      List<AnnotationDataset> annotationDataset, PartitionedVdbArchiveReader archiveReader) {
    throw new RuntimeException("Not implemented");
  }

  private VcfRecordAnnotator createRecordAnnotator(
      AnnotationSpec annotationSpec, PartitionedVdbArchiveReader archiveReader) {
    AnnotationSchema annotationSchema = annotationSpec.annotationSchema();
    return switch (annotationSchema.annotationType()) {
      case SEQUENCE_VARIANT -> {
        List<AnnotationDataset> annotationDatasets = annotationSchema.annotationDatasets();
        yield switch (annotationDatasets.size()) {
          case 0 -> throw new IllegalStateException();
          case 1 -> {
            SequenceVariantAnnotationIndexDispatcherReaderFactory<SequenceVariant>
                indexDispatcherReaderFactory =
                    SequenceVariantAnnotationIndexDispatcherReaderFactory.create();
            MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader =
                indexDispatcherReaderFactory.createReader();

            AnnotationIndexReader<SequenceVariant> annotationIndexReader =
                new SequenceVariantAnnotationIndexReader<>(
                    new AnnotationBlobReader("idx", archiveReader), indexReader);
            AnnotationDatasetReader<ScalarAnnotation> annotationDatasetReader =
                createScalarAnnotationDatasetReader(annotationDatasets.getFirst(), archiveReader);
            SequenceVariantAnnotationDb<SequenceVariant, ScalarAnnotation> annotationDb =
                new SequenceVariantAnnotationDb<>(
                    new PartitionResolver(),
                    annotationIndexReader,
                    annotationDatasetReader,
                    (_) -> true); // FIXME hardcoded predicate

            ScalarAnnotationSelector annotationSelector =
                candidateAnnotations ->
                    switch (candidateAnnotations.size()) {
                      case 0 -> null;
                      case 1 -> candidateAnnotations.getFirst();
                      default ->
                          NumberCollections.findMax(
                              candidateAnnotations,
                              scalarAnnotation ->
                                  switch (scalarAnnotation) {
                                    case ScalarAnnotation.DoubleAnnotation doubleAnnotation ->
                                        doubleAnnotation.getValue();
                                    case ScalarAnnotation.NullableDoubleAnnotation
                                            nullableDoubleAnnotation ->
                                        nullableDoubleAnnotation.isNull()
                                            ? null
                                            : nullableDoubleAnnotation.getValue();
                                    default ->
                                        throw new IllegalStateException(
                                            "Unexpected value: " + scalarAnnotation); // FIXME
                                  });
                    };

            Predicate<SequenceVariant> canAnnotate =
                sequenceVariant ->
                    annotationSchema.supportedVariantTypes().contains(sequenceVariant.getType());

            yield new ScalarIntervalVcfRecordAnnotator(
                canAnnotate,
                annotationDb,
                annotationSelector,
                new VcfRecordAnnotationWriter(
                    ((VcfOutputFormat) annotationSpec.outputFormat()).infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
          default -> throw new RuntimeException("Not implemented");
        };
      }
      case POSITION -> {
        List<AnnotationDataset> annotationDatasets = annotationSchema.annotationDatasets();
        yield switch (annotationDatasets.size()) {
          case 0 -> throw new IllegalStateException();
          case 1 -> {
            AnnotationDatasetReader<ScalarAnnotation> annotationDatasetReader =
                createScalarAnnotationDatasetReader(annotationDatasets.getFirst(), archiveReader);
            IntervalAnnotationDb<SequenceVariant, ScalarAnnotation> annotationDb =
                new IntervalAnnotationDb<>(new PartitionResolver(), annotationDatasetReader);

            ScalarAnnotationSelector annotationSelector =
                candidateAnnotations ->
                    switch (candidateAnnotations.size()) {
                      case 0 -> null;
                      case 1 -> candidateAnnotations.getFirst();
                      default ->
                          NumberCollections.findMax(
                              candidateAnnotations,
                              scalarAnnotation ->
                                  switch (scalarAnnotation) {
                                    case ScalarAnnotation.DoubleAnnotation doubleAnnotation ->
                                        doubleAnnotation.getValue();
                                    case ScalarAnnotation.NullableDoubleAnnotation
                                            nullableDoubleAnnotation ->
                                        nullableDoubleAnnotation.isNull()
                                            ? null
                                            : nullableDoubleAnnotation.getValue();
                                    default ->
                                        throw new IllegalStateException(
                                            "Unexpected value: " + scalarAnnotation); // FIXME
                                  });
                    };

            Predicate<SequenceVariant> canAnnotate =
                sequenceVariant ->
                    annotationSchema.supportedVariantTypes().contains(sequenceVariant.getType());

            yield new ScalarIntervalVcfRecordAnnotator<>(
                canAnnotate,
                annotationDb,
                annotationSelector,
                new VcfRecordAnnotationWriter(
                    ((VcfOutputFormat) annotationSpec.outputFormat()).infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
          default -> throw new RuntimeException("Not implemented");
        };
      }
    };
  }

  public static VcfAnnotationModuleLoader create() {
    return new VcfAnnotationModuleLoader(
        PartitionedVdbArchiveReaderFactory.create(), AnnotationSpecLoader.create());
  }
}
