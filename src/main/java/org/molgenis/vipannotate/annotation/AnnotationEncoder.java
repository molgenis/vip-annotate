package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryWriter;

public interface AnnotationEncoder<T extends Annotation> {
  /** Encode an annotation into the given {@link BinaryWriter}. */
  void encodeInto(T annotation, BinaryWriter binaryWriter);

  long getEncodedSizeInBytes();
}
