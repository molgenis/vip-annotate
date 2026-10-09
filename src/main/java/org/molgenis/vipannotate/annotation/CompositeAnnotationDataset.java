package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotationDataset implements AnnotationDataset<CompositeAnnotation> {
  private final AnnotationDataset<? extends Annotation>[] annotationDatasets;

  // TODO possible to get rid of cast?
  @SuppressWarnings("unchecked")
  @Override
  public void findByIndexInto(int index, CompositeAnnotation annotation) {
    for (int i = 0, length = annotationDatasets.length; i < length; i++) {
      ((AnnotationDataset<Annotation>) annotationDatasets[i])
          .findByIndexInto(index, annotation.annotation(i));
    }
  }
}
