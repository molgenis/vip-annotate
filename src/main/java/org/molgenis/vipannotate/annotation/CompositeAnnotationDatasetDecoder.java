package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<CompositeAnnotation> {
  private final AnnotationDatasetDecoder<?>[] datasetReaders;

  @Override
  public AnnotationDataset<CompositeAnnotation> decode(MemoryDecoderProvider decoderProvider) {
    AnnotationDataset<? extends Annotation>[] annotationDatasets =
        new AnnotationDataset<?>[datasetReaders.length];
    for (int i = 0, length = datasetReaders.length; i < length; i++) {
      annotationDatasets[i] = datasetReaders[i].decode(decoderProvider);
    }
    return new CompositeAnnotationDataset(annotationDatasets);
  }
}
