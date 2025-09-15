package org.molgenis.vipannotate.annotation;

import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.IndexRange;

public interface IndexedAnnotationEncoder<T extends Annotation>
    extends AnnotationEncoder<IndexedAnnotation<T>> {
  void clear(IndexRange indexRange, MemoryBuffer memoryBuffer);
}
