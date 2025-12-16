package org.molgenis.vipannotate.format.vdb;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class VdbArchiveIndexReader implements MemoryBufferReader<VdbArchiveIndex> {

  @Override
  public VdbArchiveIndex readFrom(MemoryBuffer memBuffer) {
    memBuffer.flip();

    int nrEntries = memBuffer.getInt();
    VdbArchiveIndex archiveIndex = new VdbArchiveIndex(nrEntries);
    fillIndex(memBuffer, nrEntries, archiveIndex);
    return archiveIndex;
  }

  @Override
  public void readInto(MemoryBuffer memBuffer, VdbArchiveIndex archiveIndex) {
    memBuffer.flip();

    int nrEntries = memBuffer.getInt();
    archiveIndex.reset();
    fillIndex(memBuffer, nrEntries, archiveIndex);
  }

  private void fillIndex(MemoryBuffer memoryBuffer, int nrEntries, VdbArchiveIndex archiveIndex) {
    for (int i = 0; i < nrEntries; i++) {
      byte[] bytes = memoryBuffer.getByteArray();
      int entryId = memoryBuffer.getInt();
      archiveIndex.addEntry(new String(bytes, UTF_8), entryId);
    }
  }
}
