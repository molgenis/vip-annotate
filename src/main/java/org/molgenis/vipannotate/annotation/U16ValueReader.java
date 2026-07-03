package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

public final class U16ValueReader implements IntValueReader {
  public int apply(MemoryBuffer memoryBuffer, int valueIndex) {
    return memoryBuffer.getUnsignedShortAtIndex(valueIndex);
  }
}
