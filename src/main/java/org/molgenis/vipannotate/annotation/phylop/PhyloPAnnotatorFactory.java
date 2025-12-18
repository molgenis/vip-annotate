package org.molgenis.vipannotate.annotation.phylop;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;
import org.molgenis.vipannotate.util.DoubleCodec;

public class PhyloPAnnotatorFactory extends PositionAnnotatorFactory<DoubleValueAnnotation> {
  public PhyloPAnnotatorFactory(
      PartitionedVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver) {
    super(archiveReaderFactory, partitionResolver);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    PartitionedVdbArchiveReader archiveReader = createArchiveReader(annotationsDir, "phylop.zip");

    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            new PositionScoreAnnotationDatasetFactory(
                new PhyloPAnnotationDecoder(new DoubleCodec())),
            new AnnotationBlobReader("score", archiveReader));

    PositionAnnotationDb<DoubleValueAnnotation> annotationDb =
        buildAnnotationDb(
            annotationDatasetReader, EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new PhyloPAnnotator(annotationDb);
  }
}
