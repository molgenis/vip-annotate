package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class PerElementAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final BinaryReader binaryReader;

  @Override
  public void findByIndexInto(int index, T annotation) {
    annotationDecoder.decodeInto(binaryReader, index, annotation);
  }
}
