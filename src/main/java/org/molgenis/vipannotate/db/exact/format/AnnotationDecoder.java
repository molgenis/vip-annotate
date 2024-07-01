package org.molgenis.vipannotate.db.exact.format;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationDecoder<T> {
  T decode(MemoryBuffer memoryBuffer);
}
