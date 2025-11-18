package org.molgenis.vipannotate.annotation.remm;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;
import org.molgenis.vipannotate.util.DoubleCodec;

public class RemmAnnotatorFactory extends PositionAnnotatorFactory<DoubleValueAnnotation> {
  public RemmAnnotatorFactory(
      PartitionedVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver) {
    super(archiveReaderFactory, partitionResolver);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    PartitionedVdbArchiveReader archiveReader = createArchiveReader(annotationsDir, "remm.zip");

    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            new PositionScoreAnnotationDatasetFactory(new RemmAnnotationDecoder(new DoubleCodec())),
            new AnnotationBlobReader("score", archiveReader));

    @SuppressWarnings("DataFlowIssue")
    PositionAnnotationDb<DoubleValueAnnotation> annotationDb =
        buildAnnotationDb(
            annotationDatasetReader, EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new RemmAnnotator(annotationDb);
  }
}
