package org.molgenis.vipannotate.format.vdb;

import org.molgenis.vipannotate.format.vdb.VdbArchiveMetadata.VdbArchiveMetadataBuilder;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

public class VdbArchiveMetadataReader {
  public VdbArchiveMetadata readFrom(MemoryBuffer memBuffer) {
    int nrRecords = memBuffer.getInt();

    VdbArchiveMetadataBuilder builder = new VdbArchiveMetadataBuilder(nrRecords);
    long prevOffset = 0;
    for (int i = 0; i < nrRecords; i++) {
      // delta decode offset
      long offset = prevOffset + memBuffer.getLong();
      prevOffset = offset;

      long length = memBuffer.getLong();
      Compression compression = Compression.fromValue(memBuffer.getByte());
      IoMode ioMode = IoMode.fromValue(memBuffer.getByte());
      builder.addEntry(offset, length, compression, ioMode);
    }

    return builder.build();
  }
}
