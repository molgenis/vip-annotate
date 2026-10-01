package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryEncoder;

public interface AnnotationEncoder<T extends Annotation> {
  /** Encode an annotation into the given {@link MemoryEncoder}. */
  void encodeInto(T annotation, MemoryEncoder memEncoder);

  long getEncodedSizeInBytes();
}
