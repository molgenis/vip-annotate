package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class ScalarAnnotationDataset implements AnnotationDataset<ScalarAnnotation> {
  private final AnnotationDecoder<ScalarAnnotation> annotationDecoder;
  private final MemoryBuffer memoryBuffer;

  @Override
  public @Nullable ScalarAnnotation findByIndex(int index) {
    return annotationDecoder.decode(memoryBuffer, index);
  }
}
