package org.molgenis.vipannotate.format.vdb;

import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

public class VdbArchiveIndexReader implements MemoryDecodable<VdbArchiveIndex> {

  @Override
  public VdbArchiveIndex readFrom(MemoryDecoder memDecoder) {
    memDecoder.unwrap().flip(); // FIXME should not happen here

    VdbArchiveIndex archiveIndex = new VdbArchiveIndex();
    memDecoder.getMapInto(
        archiveIndex.getEntryNameToIdMap(), MemoryDecoder::getString, MemoryDecoder::getInt);
    return archiveIndex;
  }

  @Override
  public void readInto(MemoryDecoder memDecoder, VdbArchiveIndex archiveIndex) {
    memDecoder.unwrap().flip(); // FIXME should not happen here

    archiveIndex.reset();
    memDecoder.getMapInto(
        archiveIndex.getEntryNameToIdMap(), MemoryDecoder::getString, MemoryDecoder::getInt);
  }
}
