package org.molgenis.vipannotate.annotation;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class PerElementAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final BinaryReader binaryReader;

  @Override
  public T findByIndexInto(int index, Supplier<T> annotationSupplier) {
    T annotation = annotationSupplier.get();
    annotationDecoder.decodeInto(binaryReader, index, annotation);
    return annotation;
  }
}
