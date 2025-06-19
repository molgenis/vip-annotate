package org.molgenis.vipannotate.annotation.phylop;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class PhyloPAnnotatorFactory {
  @NonNull private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("phylop.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    ContigPosScoreAnnotationDatasetFactory annotationDatasetFactory =
        new ContigPosScoreAnnotationDatasetFactory(
            new PhyloPAnnotationDatasetDecoder(new PhyloPAnnotationDataCodec()));
    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new ContigPosScoreAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    return new PhyloPAnnotator(new GenomePositionAnnotationDb<>(annotationDatasetReader));
  }
}
