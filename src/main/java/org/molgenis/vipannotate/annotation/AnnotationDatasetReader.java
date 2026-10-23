package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

/// reads annotation datasets
public interface AnnotationDatasetReader<T extends Annotation> extends AutoCloseableNoThrow {
  /// reads annotation dataset for the given partition key
  AnnotationDataset<T> read(PartitionKey partitionKey);
}
