package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
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
    VcfRecordAnnotator<?> recordAnnotator = createRecordAnnotator(annotationSpec, archiveReader);
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

  private <T extends Annotation> AnnotationDatasetDecoder<T> createAnnotationDatasetReader(
      AnnotationDataset annotationDataset, PartitionedVdbArchiveReader archiveReader) {
    AnnotationValue annotationValue = annotationDataset.annotationValue();
    AnnotationBlobReader blobReader =
        new AnnotationBlobReader(annotationDataset.id(), archiveReader);
    return (AnnotationDatasetDecoder<T>)
        switch (annotationValue.logicalType()) {
          case EnumLogicalType enumLogicalType ->
              createEnumAnnotationDatasetReader(enumLogicalType, blobReader);
          case EnumSetLogicalType enumSetLogicalType ->
              createEnumSetAnnotationDatasetReader(enumSetLogicalType, blobReader);
          case ScalarLogicalType scalarLogicalType ->
              createScalarAnnotationDatasetReader(annotationDataset, blobReader);
        };
  }

  private EnumAnnotationDatasetReader createEnumAnnotationDatasetReader(
      EnumLogicalType logicalType, AnnotationBlobReader blobReader) {
    return new EnumAnnotationDatasetReader(logicalType, blobReader);
  }

  private AnnotationDatasetDecoder<StringListAnnotation> createEnumSetAnnotationDatasetReader(
      EnumSetLogicalType logicalType, AnnotationBlobReader blobReader) {
    return new EnumSetAnnotationDatasetDecoder(logicalType, blobReader);
  }

  private ScalarAnnotationDatasetReader createScalarAnnotationDatasetReader(
      AnnotationDataset annotationDataset, AnnotationBlobReader blobReader) {
    AnnotationDecoder<ScalarAnnotation> annotationDecoder =
        createAnnotationDecoder(annotationDataset.annotationValue());
    return new ScalarAnnotationDatasetReader(annotationDecoder, blobReader);
  }

  private AnnotationDecoder<ScalarAnnotation> createAnnotationDecoder(
      AnnotationValue annotationValue) {
    if (annotationValue.encoding() == null) {
      // FIXME handle other logical types
      ScalarLogicalType logicalType = (ScalarLogicalType) annotationValue.logicalType();
      if (logicalType.nullable()) {
        // FIXME support  nullable logicalType encoding when encoding is null
        throw new UnsupportedOperationException();
      }

      StorageType storageType = annotationValue.storageType();
      ReadValueFunction readValueFunction = createReadValueFunction(storageType);

      return switch (storageType.scalarType()) {
        case I8, I16, I32, U8, U16 ->
            new AnnotationDecoder<>() {
              @Override
              public IntAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
                int value = readValueFunction.apply(memBuffer, annotationIndex);
                return new IntAnnotation(value);
              }

              @Override
              public void decodeInto(
                  MemoryBuffer memBuffer, int annotationIndex, ScalarAnnotation annotation) {
                // FIXME implement AnnotationDecoder.decodeInto
                throw new UnsupportedOperationException();
              }
            };

        case I64, U32, U64, F32, F64 -> {
          // FIXME support null encoding for U64,F32,F64
          throw new UnsupportedOperationException();
        }
      };
    }

    return switch (annotationValue.encoding()) {
      case EnumEncoding enumEncoding -> {
        // FIXME implement
        throw new UnsupportedOperationException();
      }
      case QuantizedEncoding quantizedEncoding ->
          createQuantizedAnnotationDecoder(
              annotationValue.storageType(),
              // FIXME remove cast
              (ScalarLogicalType) annotationValue.logicalType(),
              quantizedEncoding);
    };
  }

  private static ReadValueFunction createReadValueFunction(StorageType storageType) {
    return switch (storageType.scalarType()) {
      case I8 -> MemoryBuffer::getByteAtIndex;
      case I16 -> MemoryBuffer::getShortAtIndex;
      case I32 -> MemoryBuffer::getIntAtIndex;
      case U8 -> MemoryBuffer::getUnsignedByteAtIndex;
      case U16 -> MemoryBuffer::getUnsignedShortAtIndex;
      default -> throw new IllegalArgumentException();
    };
  }

  private static QuantizedAnnotationDecoder createQuantizedAnnotationDecoder(
      StorageType storageType, ScalarLogicalType logicalType, QuantizedEncoding encoding) {
    Quantizer quantizer = createQuantizer(logicalType, encoding);

    ReadValueFunction readValueFunction = createReadValueFunction(storageType);
    return new QuantizedAnnotationDecoder(quantizer, readValueFunction, encoding.nullCode());
  }

  private static Quantizer createQuantizer(
      ScalarLogicalType logicalType, QuantizedEncoding encoding) {
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

  private AnnotationDatasetDecoder<CompositeAnnotation> createCompositeAnnotationDatasetReader(
      List<AnnotationDataset> annotationDatasets, PartitionedVdbArchiveReader archiveReader) {
    AnnotationDatasetDecoder<?>[] annotationDatasetReaders =
        new AnnotationDatasetDecoder[annotationDatasets.size()];
    for (int i = 0; i < annotationDatasets.size(); i++) {
      annotationDatasetReaders[i] =
          createAnnotationDatasetReader(annotationDatasets.get(i), archiveReader);
    }
    return new CompositeAnnotationDatasetReader(annotationDatasetReaders);
  }

  private VcfRecordAnnotator<?> createRecordAnnotator(
      AnnotationSpec annotationSpec, PartitionedVdbArchiveReader archiveReader) {
    AnnotationSchema annotationSchema = annotationSpec.annotationSchema();

    Predicate<SequenceVariant> canAnnotate =
        sequenceVariant ->
            annotationSchema.supportedVariantTypes().contains(sequenceVariant.getType());

    return switch (annotationSchema.annotationType()) {
      case SEQUENCE_VARIANT -> {
        List<AnnotationDataset> annotationDatasets = annotationSchema.annotationDatasets();

        SequenceVariantAnnotationIndexDispatcherReaderFactory<SequenceVariant>
            indexDispatcherReaderFactory =
                SequenceVariantAnnotationIndexDispatcherReaderFactory.create();
        MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader =
            indexDispatcherReaderFactory.createReader();

        AnnotationIndexReader<SequenceVariant> annotationIndexReader =
            new SequenceVariantAnnotationIndexReader<>(
                new AnnotationBlobReader("idx", archiveReader), indexReader);

        PartitionResolver partitionResolver = new PartitionResolver();

        yield switch (annotationDatasets.size()) {
          case 0 -> throw new IllegalStateException();
          //          case 1 -> {
          //            // FIXME only works for scalar now
          //            AnnotationDatasetReader<ScalarAnnotation> annotationDatasetReader =
          //                (AnnotationDatasetReader<ScalarAnnotation>)
          //                    (AnnotationDatasetReader<?>)
          //                        createAnnotationDatasetReader(annotationDatasets.getFirst(),
          // archiveReader);
          //
          //            SequenceVariantAnnotationDb<SequenceVariant, ScalarAnnotation> annotationDb
          // =
          //                new SequenceVariantAnnotationDb<>(
          //                    partitionResolver, annotationIndexReader, annotationDatasetReader);
          //
          //            ScalarAnnotationSelector annotationSelector =
          // createScalarAnnotationSelector();
          //
          //            yield new VcfRecordAnnotator<>(
          //                new SequenceVariantAnnotator<>(canAnnotate, annotationDb,
          // annotationSelector),
          //                new VcfRecordAnnotationWriter<>(
          //                    ((VcfOutputFormat) annotationSpec.outputFormat()).infoId()), //
          // FIXME hardcoded
          //                new VcfContigResolver()); // FIXME annotationId != infoId
          //          }
          default -> {
            AnnotationDatasetDecoder<CompositeAnnotation> annotationDatasetReader =
                createCompositeAnnotationDatasetReader(annotationDatasets, archiveReader);

            SequenceVariantAnnotationDb<SequenceVariant, CompositeAnnotation> annotationDb =
                new SequenceVariantAnnotationDb<>(
                    partitionResolver, annotationIndexReader, annotationDatasetReader);

            yield new VcfRecordAnnotator<>(
                new SequenceVariantAnnotator<>(
                    canAnnotate,
                    annotationDb,
                    (annotationList) -> {
                      if (annotationList.isEmpty()) {
                        return null;
                      } else if (annotationList.size() == 1) {
                        return annotationList.getFirst();
                      } else {
                        // FIXME implement annotation selector for composite annotations
                        throw new UnsupportedOperationException();
                      }
                    }),
                new VcfRecordAnnotationWriter<>(
                    ((VcfOutputFormat) annotationSpec.outputFormat()).infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
        };
      }
      case POSITION -> {
        List<AnnotationDataset> annotationDatasets = annotationSchema.annotationDatasets();
        yield switch (annotationDatasets.size()) {
          case 0 -> throw new IllegalStateException();
          case 1 -> {
            AnnotationDatasetDecoder<ScalarAnnotation> annotationDatasetReader =
                createAnnotationDatasetReader(annotationDatasets.getFirst(), archiveReader);
            IntervalAnnotationDb<SequenceVariant, ScalarAnnotation> annotationDb =
                new IntervalAnnotationDb<>(new PartitionResolver(), annotationDatasetReader);

            ScalarAnnotationSelector annotationSelector = createScalarAnnotationSelector();

            yield new VcfRecordAnnotator<>(
                new SequenceVariantAnnotator<>(canAnnotate, annotationDb, annotationSelector),
                new VcfRecordAnnotationWriter<>(
                    ((VcfOutputFormat) annotationSpec.outputFormat()).infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
          default -> throw new RuntimeException("Not implemented");
        };
      }
    };
  }

  private static @NonNull ScalarAnnotationSelector createScalarAnnotationSelector() {
    return candidateAnnotations ->
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
                        case ScalarAnnotation.NullableDoubleAnnotation nullableDoubleAnnotation ->
                            nullableDoubleAnnotation.isNull()
                                ? null
                                : nullableDoubleAnnotation.getValue();
                        default ->
                            throw new IllegalStateException(
                                "Unexpected value: " + scalarAnnotation); // FIXME
                      });
        };
  }

  public static VcfAnnotationModuleLoader create() {
    return new VcfAnnotationModuleLoader(
        PartitionedVdbArchiveReaderFactory.create(), AnnotationSpecLoader.create());
  }
}
