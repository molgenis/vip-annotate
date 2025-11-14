package org.molgenis.vipannotate.annotation.phylop;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.util.DoubleCodec;

public class PhyloPAnnotatorFactory extends PositionAnnotatorFactory<DoubleValueAnnotation> {
  public PhyloPAnnotatorFactory(
      AnnotationVdbArchiveReaderFactory archiveReaderFactory, PartitionResolver partitionResolver) {
    super(archiveReaderFactory, partitionResolver);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    AnnotationVdbArchiveReader archiveReader = createArchiveReader(annotationsDir, "phylop.zip");

    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            new PositionScoreAnnotationDatasetFactory(
                new PhyloPAnnotationDecoder(new DoubleCodec())),
            new AnnotationBlobReader("score", archiveReader));

    @SuppressWarnings("DataFlowIssue")
    PositionAnnotationDb<DoubleValueAnnotation> annotationDb =
        buildAnnotationDb(
            annotationDatasetReader, EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new PhyloPAnnotator(annotationDb);
  }
}
