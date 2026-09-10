package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record ResolvedEnumSetAnnotationSpec(@Nullable String description, String[] values)
    implements ResolvedAnnotationSpec {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    binaryWriter.writeArray(values, BinaryWriter::writeString);
  }

  public static ResolvedEnumSetAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    String[] values = binaryReader.readArray(String[]::new, BinaryReader::readString);
    return new ResolvedEnumSetAnnotationSpec(description, values);
  }
}
