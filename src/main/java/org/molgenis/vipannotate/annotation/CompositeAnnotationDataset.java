package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class CompositeAnnotationDataset implements AnnotationDataset<CompositeAnnotation> {
  private final AnnotationDataset<ScalarAnnotation>[] annotationDatasets;

  @Override
  public @Nullable CompositeAnnotation findByIndex(int index) {
    ScalarAnnotation[] scalarAnnotations = new ScalarAnnotation[annotationDatasets.length];
    for (int i = 0, length = annotationDatasets.length; i < length; i++) {
      scalarAnnotations[i] = annotationDatasets[i].findByIndex(index);
    }
    return new CompositeAnnotation(scalarAnnotations);
  }
}
