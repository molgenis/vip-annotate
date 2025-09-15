package org.molgenis.vipannotate.annotation.remm;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.util.DoubleCodec;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class RemmAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("remm.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    PositionScoreAnnotationDatasetFactory annotationDatasetFactory =
        new PositionScoreAnnotationDatasetFactory(new RemmAnnotationDecoder(new DoubleCodec()));
    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    return new RemmAnnotator(
        new PositionAnnotationDb<>(annotationDatasetReader), vcfRecordAnnotationWriter);
  }
}
