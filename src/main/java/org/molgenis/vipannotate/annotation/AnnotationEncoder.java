package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationEncoder<T extends Annotation> {
  /**
   * @return the size of one annotation in bytes
   */
  int getAnnotationSizeInBytes();

  void encode(T annotation, @NonNull MemoryBuffer memoryBuffer);
}
