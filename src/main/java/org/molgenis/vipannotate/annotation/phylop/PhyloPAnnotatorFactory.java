package org.molgenis.vipannotate.annotation.phylop;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.util.DoubleCodec;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class PhyloPAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("phylop.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    PositionScoreAnnotationDatasetFactory annotationDatasetFactory =
        new PositionScoreAnnotationDatasetFactory(new PhyloPAnnotationDecoder(new DoubleCodec()));
    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new PositionScoreAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    return new PhyloPAnnotator(
        new PositionAnnotationDb<>(annotationDatasetReader), vcfRecordAnnotationWriter);
  }
}
