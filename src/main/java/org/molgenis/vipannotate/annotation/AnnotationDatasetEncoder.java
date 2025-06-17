package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

public interface AnnotationDatasetEncoder<T> {
  MemoryBuffer encode(SizedIterator<T> annotationIterator, int maxAnnotations);
}
