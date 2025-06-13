package org.molgenis.vipannotate.db.v2;

// FIXME AutoClosable?
public interface AnnotationDatasetReader<T> {
  /**
   * @return annotation data set, never <code>null</code>
   */
  AnnotationDataset<T> read(GenomePartitionKey genomePartitionKey);
}
