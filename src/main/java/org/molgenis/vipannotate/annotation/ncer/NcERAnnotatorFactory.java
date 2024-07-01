package org.molgenis.vipannotate.annotation.ncer;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.util.DoubleCodec;

public class NcERAnnotatorFactory extends PositionAnnotatorFactory<DoubleValueAnnotation> {
  public NcERAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver) {
    super(annotationBlobReaderFactory, partitionResolver);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    MappableZipFile zipFile = loadZipFile(annotationsDir, "ncer.zip");

    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            new PositionScoreAnnotationDatasetFactory(new NcERAnnotationDecoder(new DoubleCodec())),
            annotationBlobReaderFactory.create(zipFile, "score"));

    @SuppressWarnings("DataFlowIssue")
    PositionAnnotationDb<DoubleValueAnnotation> annotationDb =
        buildAnnotationDb(
            annotationDatasetReader, EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new NcERAnnotator(annotationDb);
  }
}
