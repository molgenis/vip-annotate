package org.molgenis.vipannotate.annotation;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.zip.MappableZipFile;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AnnotatorFactory {
  protected final AnnotationBlobReaderFactory annotationBlobReaderFactory;
  protected final PartitionResolver partitionResolver;

  public abstract VcfRecordAnnotator create(Path annotationsDir);

  protected MappableZipFile loadZipFile(Path dir, String filename) {
    Path file = dir.resolve(filename);
    if (Files.notExists(file)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(file));
    }
    return MappableZipFile.fromFile(file);
  }
}
