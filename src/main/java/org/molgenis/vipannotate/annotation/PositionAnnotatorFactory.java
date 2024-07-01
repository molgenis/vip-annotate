package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;

public abstract class PositionAnnotatorFactory<U extends Annotation> extends AnnotatorFactory {
  public PositionAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver) {
    super(annotationBlobReaderFactory, partitionResolver);
  }

  protected PositionAnnotationDb<U> buildAnnotationDb(
      AnnotationDatasetReader<U> annotationDatasetReader,
      EnumSet<SequenceVariantType> variantTypes) {
    return new PositionAnnotationDb<>(
        partitionResolver,
        annotationDatasetReader,
        sequenceVariant -> variantTypes.contains(sequenceVariant.getType()));
  }
}
