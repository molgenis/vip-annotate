package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record PositionBinSpec(long offset, byte bits) {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeLong(offset);
    binaryWriter.writeByte(bits);
  }

  public static PositionBinSpec readFrom(BinaryReader binaryReader) {
    long offset = binaryReader.readLong();
    byte bits = binaryReader.getByte();
    return new PositionBinSpec(offset, bits);
  }
}
