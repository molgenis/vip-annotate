package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record ResolvedIntAnnotationSpec(
    @Nullable String description, IntType storageType, IntEncoding intEncoding)
    implements ResolvedAnnotationSpec {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    binaryWriter.writeEnum(storageType);
    IntEncoding.writeTo(binaryWriter, intEncoding);
  }

  public static ResolvedIntAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    IntType intType = binaryReader.readEnum(IntType.class);
    IntEncoding intEncoding = IntEncoding.readFrom(binaryReader);
    return new ResolvedIntAnnotationSpec(description, intType, intEncoding);
  }
}
