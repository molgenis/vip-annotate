package org.molgenis.vipannotate.format.vdb;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@RequiredArgsConstructor
public class VdbArchiveIndexWriter implements MemoryBufferWriter<VdbArchiveIndex> {
  private final VdbMemoryBufferFactory memBufferFactory;

  @Override
  public MemoryBuffer writeTo(VdbArchiveIndex archiveIndex) {
    MemoryBuffer memBuffer = memBufferFactory.newMemoryBuffer();
    writeInto(archiveIndex, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(VdbArchiveIndex archiveIndex, MemoryBuffer memBuffer) {
    Set<Map.Entry<String, Integer>> entries = archiveIndex.getEntryNameToIdMap().entrySet();
    memBuffer.putInt(entries.size());
    for (Map.Entry<String, Integer> entry : entries) {
      memBuffer.putByteArray(entry.getKey().getBytes(UTF_8));
      memBuffer.putInt(entry.getValue());
    }
  }
}
