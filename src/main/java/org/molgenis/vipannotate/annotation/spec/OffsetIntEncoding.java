package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record OffsetIntEncoding(int offset) implements IntEncoding {
  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putVarInt(offset);
  }

  public static OffsetIntEncoding readFrom(MemoryDecoder memDecoder) {
    return new OffsetIntEncoding(memDecoder.getVarInt());
  }
}
