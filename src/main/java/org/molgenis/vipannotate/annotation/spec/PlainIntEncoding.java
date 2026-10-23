package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record PlainIntEncoding() implements IntEncoding {
  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    // no op
  }
}
