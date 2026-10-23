package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record FloatAnnotationSpec(
    @Nullable String description,
    boolean nullable,
    ScalarType storageType,
    FloatEncoding floatEncoding)
    implements AnnotationSpec {
  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putStringNullable(description);
    memEncoder.putBoolean(nullable);
    ScalarType.writeTo(memEncoder, storageType);
    FloatEncoding.writeTo(memEncoder, floatEncoding);
  }

  public static FloatAnnotationSpec readFrom(MemoryDecoder memDecoder) {
    String description = memDecoder.getStringNullable();
    boolean nullable = memDecoder.getBoolean();
    ScalarType storageType = ScalarType.readFrom(memDecoder);
    FloatEncoding floatEncoding = FloatEncoding.readFrom(memDecoder);
    return new FloatAnnotationSpec(description, nullable, storageType, floatEncoding);
  }
}
