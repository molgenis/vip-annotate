package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record EnumSetAnnotationSpec(@Nullable String description, String[] values)
    implements AnnotationSpec {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    binaryWriter.writeArray(values, BinaryWriter::writeString);
  }

  public static EnumSetAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    String[] values = binaryReader.readArray(String[]::new, BinaryReader::readString);
    return new EnumSetAnnotationSpec(description, values);
  }
}
