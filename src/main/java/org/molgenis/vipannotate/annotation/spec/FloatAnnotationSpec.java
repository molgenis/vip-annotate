package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record FloatAnnotationSpec(
    @Nullable String description, ScalarType storageType, FloatEncoding floatEncoding)
    implements AnnotationSpec {
  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeStringNullable(description);
    ScalarType.writeTo(binaryWriter, storageType);
    FloatEncoding.writeTo(binaryWriter, floatEncoding);
  }

  public static FloatAnnotationSpec readFrom(BinaryReader binaryReader) {
    String description = binaryReader.readStringNullable();
    ScalarType storageType = ScalarType.readFrom(binaryReader);
    FloatEncoding floatEncoding = FloatEncoding.readFrom(binaryReader);
    return new FloatAnnotationSpec(description, storageType, floatEncoding);
  }
}
