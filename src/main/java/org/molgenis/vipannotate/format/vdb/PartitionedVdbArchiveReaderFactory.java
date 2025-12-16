package org.molgenis.vipannotate.format.vdb;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartitionedVdbArchiveReaderFactory {
  private final VdbArchiveReaderFactory archiveReaderFactory;
  private final VdbArchiveIndexReader indexReader;

  public static PartitionedVdbArchiveReaderFactory create() {
    VdbArchiveReaderFactory archiveReaderFactory = VdbArchiveReaderFactory.create();
    VdbArchiveIndexReader indexReader = new VdbArchiveIndexReader();
    return new PartitionedVdbArchiveReaderFactory(archiveReaderFactory, indexReader);
  }

  public PartitionedVdbArchiveReader create(Path archivePath) {
    if (Files.notExists(archivePath)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(archivePath));
    }
    VdbArchiveReader archiveReader = archiveReaderFactory.create(archivePath);
    return new PartitionedVdbArchiveReader(archiveReader, indexReader);
  }
}
