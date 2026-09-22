package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.VcfOutputFormat;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.NumberCollections;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VcfAnnotationModuleLoader {
  private final PartitionedVdbArchiveReaderFactory archiveReaderFactory;
  private final ResolvedAnnotationDbSpecReader schemaLoader;
  private final AnnotationDatasetDecoderFactory datasetDecoderFactory;

  public VcfAnnotationModule load(Path annotationDbPath) {
    PartitionedVdbArchiveReader archiveReader = archiveReaderFactory.create(annotationDbPath);

    ResolvedAnnotationDbSpec annotationDbSpec = schemaLoader.read(archiveReader);

    VcfHeaderAnnotator headerAnnotator = createHeaderAnnotator(annotationDbSpec);
    VcfRecordAnnotator<?> recordAnnotator = createRecordAnnotator(annotationDbSpec, archiveReader);
    return new VcfAnnotationModule(headerAnnotator, recordAnnotator);
  }

  private VcfHeaderAnnotator createHeaderAnnotator(
      ResolvedAnnotationDbSpec resolvedAnnotationDbSpec) {
    // FIXME remove cast
    VcfOutputFormat output = (VcfOutputFormat) resolvedAnnotationDbSpec.outputFormat();
    return new InfoVcfHeaderAnnotator(
        output.infoId(),
        output.infoNumber(),
        output.infoType(),
        output.infoDescription(),
        AppMetadata.getName(),
        "%s+db%s".formatted(AppMetadata.getVersion(), output.infoVersion()));
  }

  private AnnotationDatasetDecoder<?> createAnnotationDatasetReader(
      String annotationDatasetId,
      ResolvedAnnotationSpec annotationSpec,
      PartitionedVdbArchiveReader archiveReader) {
    AnnotationBlobReader blobReader = new AnnotationBlobReader(annotationDatasetId, archiveReader);
    return datasetDecoderFactory.create(annotationSpec, blobReader);
  }

  private AnnotationDatasetDecoder<CompositeAnnotation> createCompositeAnnotationDatasetReader(
      ResolvedAnnotationSpecs annotationSpecs, PartitionedVdbArchiveReader archiveReader) {
    AnnotationDatasetDecoder<?>[] annotationDatasetReaders =
        new AnnotationDatasetDecoder[annotationSpecs.size()];

    AtomicInteger atomicInteger = new AtomicInteger();
    annotationSpecs.forEach(
        (annotationDatasetId, annotationSpec) ->
            annotationDatasetReaders[atomicInteger.getAndIncrement()] =
                createAnnotationDatasetReader(annotationDatasetId, annotationSpec, archiveReader));

    return new CompositeAnnotationDatasetReader(annotationDatasetReaders);
  }

  private VcfRecordAnnotator<?> createRecordAnnotator(
      ResolvedAnnotationDbSpec resolvedAnnotationDbSpec,
      PartitionedVdbArchiveReader archiveReader) {
    ResolvedAnnotationSchema annotationSchema = resolvedAnnotationDbSpec.annotationSchema();

    Predicate<SequenceVariant> canAnnotate =
        sequenceVariant ->
            annotationSchema.supportedVariantTypes().contains(sequenceVariant.getType());

    return switch (annotationSchema.annotationType()) {
      case SEQUENCE_VARIANT -> {
        ResolvedAnnotationSpecs annotationSpecs = annotationSchema.annotationSpecs();

        SequenceVariantAnnotationIndexDispatcherReaderFactory<SequenceVariant>
            indexDispatcherReaderFactory =
                SequenceVariantAnnotationIndexDispatcherReaderFactory.create();
        MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader =
            indexDispatcherReaderFactory.createReader();

        AnnotationIndexReader<SequenceVariant> annotationIndexReader =
            new SequenceVariantAnnotationIndexReader<>(
                new AnnotationBlobReader("idx", archiveReader), indexReader);

        PartitionResolver partitionResolver = new PartitionResolver();

        yield switch (annotationSpecs.size()) {
          case 0 -> throw new IllegalStateException();
          //          case 1 -> {
          //            // FIXME only works for scalar now
          //            AnnotationDatasetReader<ScalarAnnotation> annotationDatasetReader =
          //                (AnnotationDatasetReader<ScalarAnnotation>)
          //                    (AnnotationDatasetReader<?>)
          //                        createAnnotationDatasetReader(annotationSpecs.getFirst(),
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
                createCompositeAnnotationDatasetReader(annotationSpecs, archiveReader);

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
                    ((VcfOutputFormat) resolvedAnnotationDbSpec.outputFormat())
                        .infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
        };
      }
      case INTERVAL -> throw new UnsupportedOperationException(); // FIXME support
      case POSITION -> {
        ResolvedAnnotationSpecs annotationSpecs = annotationSchema.annotationSpecs();
        yield switch (annotationSpecs.size()) {
          case 0 -> throw new IllegalStateException();
          // FIXME support singular annotations
          //          case 1 -> throw new UnsupportedOperationException();
          default -> {
            AnnotationDatasetDecoder<CompositeAnnotation> annotationDatasetReader =
                createCompositeAnnotationDatasetReader(annotationSpecs, archiveReader);
            IntervalAnnotationDb<SequenceVariant, CompositeAnnotation> annotationDb =
                new IntervalAnnotationDb<>(new PartitionResolver(), annotationDatasetReader);

            ScalarAnnotationSelector annotationSelector = createScalarAnnotationSelector();

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
                    ((VcfOutputFormat) resolvedAnnotationDbSpec.outputFormat())
                        .infoId()), // FIXME hardcoded
                new VcfContigResolver()); // FIXME annotationId != infoId
          }
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
                        case ScalarAnnotation.FloatAnnotation floatAnnotation ->
                            floatAnnotation.getValue();
                        case ScalarAnnotation.NullableFloatAnnotation nullableFloatAnnotation ->
                            nullableFloatAnnotation.isNull()
                                ? null
                                : nullableFloatAnnotation.getValue();
                        default ->
                            throw new IllegalStateException(
                                "Unexpected value: " + scalarAnnotation); // FIXME
                      });
        };
  }

  public static VcfAnnotationModuleLoader create() {
    return new VcfAnnotationModuleLoader(
        PartitionedVdbArchiveReaderFactory.create(),
        ResolvedAnnotationDbSpecReader.create(),
        AnnotationDatasetDecoderFactory.create());
  }
}
