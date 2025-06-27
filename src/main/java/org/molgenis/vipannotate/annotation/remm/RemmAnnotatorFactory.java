package org.molgenis.vipannotate.annotation.remm;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class RemmAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("remm.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    PositionScoreAnnotationDatasetFactory annotationDatasetFactory =
        new PositionScoreAnnotationDatasetFactory(new RemmAnnotationDatasetDecoder());
    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    // TODO only one VcfRecordAnnotationWriter globally
    return new RemmAnnotator(
        new PositionAnnotationDb<>(annotationDatasetReader), new VcfRecordAnnotationWriter());
  }
}
