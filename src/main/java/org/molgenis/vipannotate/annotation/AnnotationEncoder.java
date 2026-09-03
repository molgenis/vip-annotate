package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface AnnotationEncoder<T extends Annotation> {
  /** Initialize */
  void initialize(MemoryBuffer memoryBuffer);

  /** Encode an annotation into the given {@link MemoryBuffer}. */
  void encodeInto(T annotation, MemoryBuffer memoryBuffer, int index);

  long getEncodedSizeInBytes();
}
