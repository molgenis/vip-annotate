package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record ResolvedEnumAnnotationSpec(
    @Nullable String description, String[] values, boolean nullable)
    implements ResolvedAnnotationSpec {

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    binaryWriter.writeArray(values, BinaryWriter::writeString);
    binaryWriter.writeBoolean(nullable);
  }

  public static ResolvedEnumAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    String[] values = binaryReader.readArray(String[]::new, BinaryReader::readString);
    boolean b = binaryReader.readBoolean();
    return new ResolvedEnumAnnotationSpec(description, values, b);
  }
}
