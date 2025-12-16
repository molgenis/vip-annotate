package org.molgenis.vipannotate.annotation;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AnnotatorFactory {
  protected final PartitionedVdbArchiveReaderFactory archiveReaderFactory;
  protected final PartitionResolver partitionResolver;

  public abstract VcfRecordAnnotator create(Path annotationsDir);

  protected PartitionedVdbArchiveReader createArchiveReader(Path annotationsDir, String filename) {
    Path file = annotationsDir.resolve(filename);
    if (Files.notExists(file)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(file));
    }
    return archiveReaderFactory.create(file);
  }
}
