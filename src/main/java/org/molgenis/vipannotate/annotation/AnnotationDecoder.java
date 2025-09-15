package org.molgenis.vipannotate.annotation;

import org.apache.fory.memory.MemoryBuffer;

public interface AnnotationDecoder<T extends Annotation> {
  T decode(MemoryBuffer memoryBuffer, int index);
}
