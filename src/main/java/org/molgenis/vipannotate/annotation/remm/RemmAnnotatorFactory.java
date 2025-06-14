package org.molgenis.vipannotate.annotation.remm;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.zip.MappableZipFile;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class RemmAnnotatorFactory {
  @NonNull private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("remm.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    ContigPosScoreAnnotationDatasetFactory annotationDatasetFactory =
        new ContigPosScoreAnnotationDatasetFactory(
            new RemmAnnotationDatasetDecoder(new RemmAnnotationDataCodec()));
    AnnotationDatasetReader<ContigPosScoreAnnotationData> annotationDatasetReader =
        new ContigPosScoreAnnotationDatasetReader(
            annotationDatasetFactory,
            annotationBlobReaderFactory.create(mappableZipFile, "scores"));

    return new RemmAnnotator(new ContigPosAnnotationDb<>(annotationDatasetReader));
  }
}
