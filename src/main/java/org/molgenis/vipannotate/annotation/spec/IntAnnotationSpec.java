package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record IntAnnotationSpec(
    @Nullable String description, boolean nullable, IntType storageType, IntEncoding intEncoding)
    implements AnnotationSpec {
  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putStringNullable(description);
    memEncoder.putBoolean(nullable);
    memEncoder.putEnum(storageType);
    IntEncoding.writeTo(memEncoder, intEncoding);
  }

  public static IntAnnotationSpec readFrom(MemoryDecoder memDecoder) {
    String description = memDecoder.getStringNullable();
    boolean nullable = memDecoder.getBoolean();
    IntType intType = memDecoder.getEnum(IntType.class);
    IntEncoding intEncoding = IntEncoding.readFrom(memDecoder);
    return new IntAnnotationSpec(description, nullable, intType, intEncoding);
  }
}
