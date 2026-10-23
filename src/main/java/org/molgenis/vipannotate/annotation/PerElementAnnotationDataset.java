package org.molgenis.vipannotate.annotation;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class PerElementAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final MemoryDecoder memDecoder;

  @Override
  public T findByIndexInto(int index, Supplier<T> annotationSupplier) {
    T annotation = annotationSupplier.get();
    annotationDecoder.decodeInto(memDecoder, index, annotation);
    return annotation;
  }
}
