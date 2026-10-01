package org.molgenis.vipannotate.annotation;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotationDataset implements AnnotationDataset<CompositeAnnotation> {
  private final AnnotationDataset<? extends Annotation>[] annotationDatasets;

  @Override
  public CompositeAnnotation findByIndexInto(
      int index, Supplier<CompositeAnnotation> annotationSupplier) {
    CompositeAnnotation compositeAnnotation = annotationSupplier.get();
    for (int i = 0, length = annotationDatasets.length; i < length; i++) {
      AnnotationDataset<Annotation> annotationDataset = getAnnotationDataset(i);
      Annotation annotation = compositeAnnotation.annotation(i);
      if (annotationDataset.findByIndexInto(index, () -> annotation) == null) {
        throw new UnsupportedOperationException();
      }
    }
    return compositeAnnotation;
  }

  @SuppressWarnings("unchecked")
  private AnnotationDataset<Annotation> getAnnotationDataset(int index) {
    return (AnnotationDataset<Annotation>) annotationDatasets[index];
  }
}
