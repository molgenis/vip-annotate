package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationEncoder<T> {
  /**
   * @return the size of one annotation in bytes
   */
  int getAnnotationSizeInBytes();

  void encode(int index, T annotation, @NonNull MemoryBuffer memoryBuffer);

  void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer);
}
