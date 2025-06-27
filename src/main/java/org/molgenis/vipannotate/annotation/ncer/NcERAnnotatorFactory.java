package org.molgenis.vipannotate.annotation.ncer;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class NcERAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("ncer.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    PositionScoreAnnotationDatasetFactory annotationDatasetFactory =
        new PositionScoreAnnotationDatasetFactory(
            new NcERAnnotationDatasetDecoder(new NcERAnnotationDataCodec()));
    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    // FIXME only one VcfRecordAnnotationWriter globally
    return new NcERAnnotator(
        new PositionAnnotationDb<>(annotationDatasetReader), new VcfRecordAnnotationWriter());
  }
}
