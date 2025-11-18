package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;

public abstract class PositionAnnotatorFactory<U extends Annotation> extends AnnotatorFactory {
  public PositionAnnotatorFactory(
      PartitionedVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver) {
    super(archiveReaderFactory, partitionResolver);
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
