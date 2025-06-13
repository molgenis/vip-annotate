package org.molgenis.vipannotate.annotation;

// FIXME AutoClosable?
public interface AnnotationDatasetReader<T> {
  /**
   * @return annotation data set, never <code>null</code>
   */
  AnnotationDataset<T> read(GenomePartitionKey genomePartitionKey);
}
