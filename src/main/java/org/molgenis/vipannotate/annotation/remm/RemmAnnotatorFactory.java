package org.molgenis.vipannotate.annotation.remm;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.util.DoubleCodec;

public class RemmAnnotatorFactory extends PositionAnnotatorFactory<DoubleValueAnnotation> {
  public RemmAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver) {
    super(annotationBlobReaderFactory, partitionResolver);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    MappableZipFile zipFile = loadZipFile(annotationsDir, "remm.zip");

    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            new PositionScoreAnnotationDatasetFactory(new RemmAnnotationDecoder(new DoubleCodec())),
            annotationBlobReaderFactory.create(zipFile, "score"));

    @SuppressWarnings("DataFlowIssue")
    PositionAnnotationDb<DoubleValueAnnotation> annotationDb =
        buildAnnotationDb(
            annotationDatasetReader, EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new RemmAnnotator(annotationDb);
  }
}
