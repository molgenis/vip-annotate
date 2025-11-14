package org.molgenis.vipannotate.annotation;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.VdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.VdbArchiveReaderFactory;

@RequiredArgsConstructor
public class AnnotationVdbArchiveReaderFactory {
  private final VdbArchiveReaderFactory archiveReaderFactory;
  private final AnnotationVdbArchiveIndexReader indexReader;

  public static AnnotationVdbArchiveReaderFactory create() {
    VdbArchiveReaderFactory archiveReaderFactory = VdbArchiveReaderFactory.create();
    AnnotationVdbArchiveIndexReader indexReader = new AnnotationVdbArchiveIndexReader();
    return new AnnotationVdbArchiveReaderFactory(archiveReaderFactory, indexReader);
  }

  public AnnotationVdbArchiveReader create(Path archivePath) {
    if (Files.notExists(archivePath)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(archivePath));
    }
    VdbArchiveReader archiveReader = archiveReaderFactory.create(archivePath);
    return AnnotationVdbArchiveReader.create(archiveReader, indexReader);
  }
}
