package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationDecoder<T> {
  T decode(MemoryBuffer memoryBuffer);
}
