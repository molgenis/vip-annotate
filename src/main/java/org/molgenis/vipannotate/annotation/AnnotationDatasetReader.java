package org.molgenis.vipannotate.annotation;

public interface AnnotationDatasetReader<T> extends AutoCloseable {
  /**
   * @return annotation data set, never <code>null</code>
   */
  AnnotationDataset<T> read(GenomePartitionKey genomePartitionKey);

  @Override
  void close();
}
