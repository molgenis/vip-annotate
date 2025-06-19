package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

public interface AnnotationDatasetEncoder<T extends Annotation> {
  MemoryBuffer encode(SizedIterator<T> annotationIt, int maxAnnotations);
}
