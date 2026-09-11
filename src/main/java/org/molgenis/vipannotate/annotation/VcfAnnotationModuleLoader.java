package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.annotation.spec.AnnotationDataset;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.NumberCollections;

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
      String annotationDatasetId,
      AnnotationDataset annotationDataset,
      PartitionedVdbArchiveReader archiveReader) {
    AnnotationBlobReader blobReader = new AnnotationBlobReader(annotationDatasetId, archiveReader);
    return (AnnotationDatasetDecoder<T>)
        switch (annotationDataset.logicalType()) {
          case EnumLogicalType enumLogicalType ->
              new EnumAnnotationDatasetReader(enumLogicalType, blobReader);
          case EnumSetLogicalType enumSetLogicalType ->
              new EnumSetAnnotationDatasetDecoder(enumSetLogicalType, blobReader);
          case ScalarLogicalType scalarLogicalType ->
              new ScalarAnnotationDatasetReader(
                  createAnnotationDecoder(annotationDataset), blobReader);
        };
  }

  private AnnotationDecoder<ScalarAnnotation> createAnnotationDecoder(
      AnnotationDataset annotationDataset) {
    return new ScalarAnnotationDecoderFactory(new ReadValueFunctionFactory())
        .create(annotationDataset);
  }

  private AnnotationDatasetDecoder<CompositeAnnotation> createCompositeAnnotationDatasetReader(
      Map<String, AnnotationDataset> annotationDatasets,
      PartitionedVdbArchiveReader archiveReader) {
    AnnotationDatasetDecoder<?>[] annotationDatasetReaders =
        new AnnotationDatasetDecoder[annotationDatasets.size()];

    int i = 0;
    for (Map.Entry<String, AnnotationDataset> entry : annotationDatasets.entrySet()) {
      String annotationDatasetId = entry.getKey();
      AnnotationDataset annotationDataset = entry.getValue();
      annotationDatasetReaders[i++] =
          createAnnotationDatasetReader(annotationDatasetId, annotationDataset, archiveReader);
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
        Map<String, AnnotationDataset> annotationDatasets = annotationSchema.annotationDatasets();

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
                        // FIXME invalid for this spliceai example
                        // #[0]CHROM       [1]POS  [2]REF  [3]ALT  [4]NCBI_GENE_ID [5]DS_AG
                        // [6]DS_AL        [7]DS_DG        [8]DS_DL        [9]DP_AG        [10]DP_AL
                        //       [11]DP_DG       [12]DP_DL
                        // chr21 29596046 A C 100379661 0.00 0.00 0.01 0.00 -2
                        // chr21 29596046 A C 2897 0.00 0.00 0.00 0.00
                        return annotationList.getFirst();
                      }
                    }),
                new VcfRecordAnnotationWriter<>(
                    ((VcfOutputFormat) annotationSpec.outputFormat()).infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
        };
      }
      case POSITION -> {
        Map<String, AnnotationDataset> annotationDatasets = annotationSchema.annotationDatasets();
        yield switch (annotationDatasets.size()) {
          case 0 -> throw new IllegalStateException();
          case 1 -> {
            AnnotationDatasetDecoder<ScalarAnnotation> annotationDatasetReader =
                createAnnotationDatasetReader(
                    annotationDatasets.keySet().iterator().next(),
                    annotationDatasets.values().iterator().next(),
                    archiveReader);
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

  private static ScalarAnnotationSelector createScalarAnnotationSelector() {
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
