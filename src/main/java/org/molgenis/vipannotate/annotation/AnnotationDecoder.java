package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryDecoder;

public interface AnnotationDecoder<T extends Annotation> {
  /** Read annotation from memory and decode into an existing annotation */
  void decodeInto(MemoryDecoder memDecoder, int annotationIndex, T annotation);
}
