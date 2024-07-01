package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

public interface AnnotationDatasetEncoder<T extends Annotation> {
  MemoryBuffer encode(SizedIterator<T> annotationIt, int maxAnnotations);
}
