package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class PerElementAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final BinaryReader binaryReader;

  @Override
  public @Nullable T findByIndex(int index) {
    return annotationDecoder.decode(binaryReader, index);
  }
}
