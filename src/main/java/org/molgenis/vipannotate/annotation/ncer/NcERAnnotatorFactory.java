package org.molgenis.vipannotate.annotation.ncer;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;

// TODO refactor: deduplicate ncer,phylop,remm factory
@RequiredArgsConstructor
public class NcERAnnotatorFactory {
  @NonNull private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("ncer.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    ContigPosScoreAnnotationDatasetFactory annotationDatasetFactory =
        new ContigPosScoreAnnotationDatasetFactory(
            new NcERAnnotationDatasetDecoder(new NcERAnnotationDataCodec()));
    AnnotationDatasetReader<DoubleValueAnnotation> annotationDatasetReader =
        new ContigPosScoreAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    return new NcERAnnotator(new GenomePositionAnnotationDb<>(annotationDatasetReader));
  }
}
