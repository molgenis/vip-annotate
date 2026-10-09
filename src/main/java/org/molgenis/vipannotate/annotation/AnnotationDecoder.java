package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryReader;

public interface AnnotationDecoder<T extends Annotation> {
  /** Read annotation from memory and decode into an existing annotation */
  void decodeInto(BinaryReader binaryReader, int annotationIndex, T annotation);
}
