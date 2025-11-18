package org.molgenis.vipannotate.format.vdb;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class VdbArchiveMetadataWriter {

  /**
   * write archive metadata to the given memory buffer that must have a capacity >= {@link
   * #calcSerializedSize(VdbArchiveMetadata)}.
   */
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
      memBuffer.putByteUnchecked((byte) entry.compression().getValue());
      memBuffer.putByteUnchecked((byte) entry.ioMode().getValue());
    }
  }

  public static long calcSerializedSize(VdbArchiveMetadata archiveMetadata) {
    // offset + length + compression method + io mode
    long recordSize = Long.BYTES + Long.BYTES + Byte.BYTES + Byte.BYTES;
    return Integer.BYTES + (archiveMetadata.getEntries().size() * recordSize);
  }
}
