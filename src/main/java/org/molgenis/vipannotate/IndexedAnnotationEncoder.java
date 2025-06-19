package org.molgenis.vipannotate;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.Annotation;
import org.molgenis.vipannotate.annotation.AnnotationEncoder;
import org.molgenis.vipannotate.annotation.IndexedAnnotation;

public interface IndexedAnnotationEncoder<T extends Annotation>
    extends AnnotationEncoder<IndexedAnnotation<T>> {
  void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer);
}
