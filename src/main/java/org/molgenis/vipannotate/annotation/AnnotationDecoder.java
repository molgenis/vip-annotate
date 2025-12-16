package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface AnnotationDecoder<T extends Annotation> {
  T decode(MemoryBuffer memoryBuffer, int index);
}
