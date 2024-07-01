package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.BinarySerializer;

public abstract class SequenceVariantAnnotatorFactory<
        T extends SequenceVariant, U extends Annotation>
    extends AnnotatorFactory {
  private final BinarySerializer<AnnotationIndex<T>> indexSerializer;

  public SequenceVariantAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver,
      BinarySerializer<AnnotationIndex<T>> indexSerializer) {
    super(annotationBlobReaderFactory, partitionResolver);
    this.indexSerializer = indexSerializer;
  }

  protected SequenceVariantAnnotationIndexReader<T> createIndexReader(MappableZipFile zip) {
    return new SequenceVariantAnnotationIndexReader<>(
        annotationBlobReaderFactory.create(zip, "idx"), indexSerializer);
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
