package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class ScalarAnnotationDataset implements AnnotationDataset<ScalarAnnotation> {
  private final AnnotationDecoder<ScalarAnnotation> annotationDecoder;
  private final BinaryReader binaryReader;

  @Override
  public @Nullable ScalarAnnotation findByIndex(int index) {
    return annotationDecoder.decode(binaryReader, index);
  }
}
