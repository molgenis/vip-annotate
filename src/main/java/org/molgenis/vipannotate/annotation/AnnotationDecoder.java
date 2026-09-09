package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface AnnotationDecoder<T extends Annotation> {
  T decode(MemoryBuffer memBuffer, int annotationIndex);

  void decodeInto(MemoryBuffer memBuffer, int annotationIndex, T annotation);
}
