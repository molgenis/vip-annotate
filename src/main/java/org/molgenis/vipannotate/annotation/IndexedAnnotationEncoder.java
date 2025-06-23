package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;

public interface IndexedAnnotationEncoder<T extends Annotation>
    extends AnnotationEncoder<IndexedAnnotation<T>> {
  void clear(int indexStart, int indexEnd, MemoryBuffer memoryBuffer);
}
