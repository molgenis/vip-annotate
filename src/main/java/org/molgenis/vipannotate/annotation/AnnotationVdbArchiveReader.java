package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vdb.VdbArchiveReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationVdbArchiveReader implements BinaryPartitionReader {
  private final VdbArchiveReader archiveReader;
  private final AnnotationVdbArchiveIndex archiveIndex;

  public static AnnotationVdbArchiveReader create(
      VdbArchiveReader archiveReader, AnnotationVdbArchiveIndexReader indexReader) {
    MemoryBuffer memBuffer = archiveReader.readLastEntry();
    memBuffer.flip();
    AnnotationVdbArchiveIndex archiveIndex = indexReader.readFrom(memBuffer);
    return new AnnotationVdbArchiveReader(archiveReader, archiveIndex);
  }

  @Override
  public @Nullable MemoryBuffer read(PartitionKey key, String annId) {
    Integer entryId = getEntryId(key, annId);
    return entryId != null ? archiveReader.readEntry(entryId) : null;
  }

  @Override
  public @Nullable MemoryBuffer readInto(PartitionKey key, String annId, MemoryBuffer memBuffer) {
    Integer entryId = getEntryId(key, annId);
    return entryId != null ? archiveReader.readEntryInto(entryId, memBuffer) : null;
  }

  private @Nullable Integer getEntryId(PartitionKey key, String annId) {
    String entryName = key.contig().getName() + "/" + key.bin() + "/" + annId;
    return archiveIndex.getEntryId(entryName);
  }

  @Override
  public void close() {
    archiveReader.close();
  }
}
