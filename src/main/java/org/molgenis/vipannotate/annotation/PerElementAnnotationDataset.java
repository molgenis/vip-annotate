package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class PerElementAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final MemoryBuffer memoryBuffer;

  @Override
  public @Nullable T findByIndex(int index) {
    return annotationDecoder.decode(memoryBuffer, index);
  }
}
