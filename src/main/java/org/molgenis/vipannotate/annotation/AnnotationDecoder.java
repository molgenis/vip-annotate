package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryReader;

public interface AnnotationDecoder<T extends Annotation> {
  T decode(BinaryReader binaryReader, int annotationIndex);

  void decodeInto(BinaryReader binaryReader, int annotationIndex, T annotation);
}
