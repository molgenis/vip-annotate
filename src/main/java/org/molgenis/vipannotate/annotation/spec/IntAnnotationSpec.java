package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record IntAnnotationSpec(
    @Nullable String description, IntType storageType, IntEncoding intEncoding)
    implements AnnotationSpec {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    binaryWriter.writeEnum(storageType);
    IntEncoding.writeTo(binaryWriter, intEncoding);
  }

  public static IntAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    IntType intType = binaryReader.readEnum(IntType.class);
    IntEncoding intEncoding = IntEncoding.readFrom(binaryReader);
    return new IntAnnotationSpec(description, intType, intEncoding);
  }
}
