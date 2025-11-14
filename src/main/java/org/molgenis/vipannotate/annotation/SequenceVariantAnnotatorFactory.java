package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public abstract class SequenceVariantAnnotatorFactory<
        T extends SequenceVariant, U extends Annotation>
    extends AnnotatorFactory {
  private final MemoryBufferReader<AnnotationIndex<T>> indexReader;

  public SequenceVariantAnnotatorFactory(
      AnnotationVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver,
      MemoryBufferReader<AnnotationIndex<T>> indexReader) {
    super(archiveReaderFactory, partitionResolver);
    this.indexReader = indexReader;
  }

  protected SequenceVariantAnnotationIndexReader<T> createIndexReader(
      AnnotationVdbArchiveReader archiveReader) {
    return new SequenceVariantAnnotationIndexReader<>(
        new AnnotationBlobReader("idx", archiveReader), indexReader);
  }

  protected SequenceVariantAnnotationDb<T, U> buildAnnotationDb(
      SequenceVariantAnnotationIndexReader<T> annotationIndexReader,
      AnnotationDatasetReader<U> datasetReader,
      EnumSet<SequenceVariantType> variantTypes) {
    return new SequenceVariantAnnotationDb<>(
        partitionResolver,
        annotationIndexReader,
        datasetReader,
        sequenceVariant -> variantTypes.contains(sequenceVariant.getType()));
  }
}
