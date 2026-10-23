package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record PlainFloatEncoding() implements FloatEncoding {
  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    // no op
  }
}
