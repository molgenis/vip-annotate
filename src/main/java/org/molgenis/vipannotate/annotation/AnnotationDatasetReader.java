package org.molgenis.vipannotate.annotation;

public interface AnnotationDatasetReader<T extends Annotation> extends AutoCloseable {
  /** {@return annotation data set, never <code>null</code>} */
  AnnotationDataset<T> read(PartitionKey partitionKey);

  @Override
  void close();
}
