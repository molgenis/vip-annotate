package org.molgenis.vipannotate.format.vdb;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@RequiredArgsConstructor
public class VdbArchiveMetadataWriter implements MemoryBufferWriter<VdbArchiveMetadata> {

  @Override
  public MemoryBuffer writeTo(VdbArchiveMetadata archiveMetadata) {
    MemoryBuffer memBuffer = MemoryBuffer.allocate(calcSerializedSize(archiveMetadata));
    writeInto(archiveMetadata, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(VdbArchiveMetadata archiveMetadata, MemoryBuffer memBuffer) {
    List<VdbArchiveMetadata.Entry> entryMetadataList = archiveMetadata.getEntries();
    memBuffer.putIntUnchecked(entryMetadataList.size());
    long prevOffset = 0;
    for (VdbArchiveMetadata.Entry entry : entryMetadataList) {
      // delta encode offset
      long deltaOffset = entry.offset() - prevOffset;
      prevOffset = entry.offset();
      memBuffer.putLongUnchecked(deltaOffset);

      memBuffer.putLongUnchecked(entry.length());
      memBuffer.putByteUnchecked((byte) entry.compressionMethod().getValue());
    }
  }

  private static long calcSerializedSize(VdbArchiveMetadata archiveMetadata) {
    Collection<VdbArchiveMetadata.Entry> vdbArchiveEntryMetadata = archiveMetadata.getEntries();
    // offset + length + compression_method
    long recordSize = Long.BYTES + Long.BYTES + Byte.BYTES;
    return Integer.BYTES + (vdbArchiveEntryMetadata.size() * recordSize);
  }
}
