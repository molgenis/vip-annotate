package org.molgenis.vipannotate.format.vdb;

import org.molgenis.vipannotate.format.vdb.VdbArchiveMetadata.VdbArchiveMetadataBuilder;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class VdbArchiveMetadataReader implements MemoryBufferReader<VdbArchiveMetadata> {
  @Override
  public VdbArchiveMetadata readFrom(MemoryBuffer memBuffer) {
    int nrRecords = memBuffer.getInt();

    VdbArchiveMetadataBuilder builder = new VdbArchiveMetadataBuilder(nrRecords);
    long prevOffset = 0;
    for (int i = 0; i < nrRecords; i++) {
      // delta decode offset
      long offset = prevOffset + memBuffer.getLong();
      prevOffset = offset;

      long length = memBuffer.getLong();
      CompressionMethod compressionMethod = CompressionMethod.fromValue(memBuffer.getByte());

      builder.addEntry(offset, length, compressionMethod);
    }

    return builder.build();
  }

  @Override
  public void readInto(MemoryBuffer memoryBuffer, VdbArchiveMetadata object) {
    throw new RuntimeException("Not implemented"); // FIXME
  }
}
