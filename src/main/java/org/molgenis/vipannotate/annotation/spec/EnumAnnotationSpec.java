package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record EnumAnnotationSpec(@Nullable String description, boolean nullable, String[] values)
    implements AnnotationSpec {

  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putStringNullable(description);
    memEncoder.putBoolean(nullable);
    memEncoder.putArray(values, MemoryEncoder::putString);
  }

  public static EnumAnnotationSpec readFrom(MemoryDecoder memDecoder) {
    String description = memDecoder.getStringNullable();
    boolean nullable = memDecoder.getBoolean();
    String[] values = memDecoder.getArray(String[]::new, MemoryDecoder::getString);
    return new EnumAnnotationSpec(description, nullable, values);
  }
}
