package org.molgenis.vipannotate.annotation.resolved;

import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record OffsetIntEncoding(int offset) implements IntEncoding {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeInt(offset);
  }

  public static OffsetIntEncoding readFrom(BinaryReader binaryReader) {
    return new OffsetIntEncoding(binaryReader.readInteger());
  }
}
