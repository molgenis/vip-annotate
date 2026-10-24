package org.molgenis.vipannotate.format.vdb;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncodable;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class VdbArchiveIndexWriter implements MemoryEncodable<VdbArchiveIndex> {
  private final VdbMemoryBufferFactory memBufferFactory;

  @Override
  public MemoryEncoder writeTo(VdbArchiveIndex archiveIndex) {
    MemoryEncoder memEncoder = MemoryEncoder.wrap(memBufferFactory.newMemoryBuffer());
    writeInto(archiveIndex, memEncoder);
    return memEncoder;
  }

  @Override
  public void writeInto(VdbArchiveIndex archiveIndex, MemoryEncoder memEncoder) {
    memEncoder.putMap(
        archiveIndex.getEntryNameToIdMap(), MemoryEncoder::putString, MemoryEncoder::putInt);
  }
}
