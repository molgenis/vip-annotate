package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record ResolvedFloatAnnotationSpec(
    @Nullable String description, ScalarType storageType, FloatEncoding floatEncoding)
    implements ResolvedAnnotationSpec {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    ScalarType.writeTo(binaryWriter, storageType);
    FloatEncoding.writeTo(binaryWriter, floatEncoding);
  }

  public static ResolvedFloatAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    ScalarType storageType = ScalarType.readFrom(binaryReader);
    FloatEncoding floatEncoding = FloatEncoding.readFrom(binaryReader);
    return new ResolvedFloatAnnotationSpec(description, storageType, floatEncoding);
  }
}
