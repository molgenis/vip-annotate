package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class CompositeAnnotationDataset implements AnnotationDataset<CompositeAnnotation> {
  private final AnnotationDataset<Annotation>[] annotationDatasets;

  @Override
  public @Nullable CompositeAnnotation findByIndex(int index) {
    Annotation[] annotations = new Annotation[annotationDatasets.length];
    for (int i = 0, length = annotationDatasets.length; i < length; i++) {
      annotations[i] = annotationDatasets[i].findByIndex(index);
    }
    return new CompositeAnnotation(annotations);
  }
}
