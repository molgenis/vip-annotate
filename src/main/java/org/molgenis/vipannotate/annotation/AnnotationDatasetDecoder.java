package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationDatasetDecoder<T> {
  T decode(MemoryBuffer memoryBuffer, int index);
}
