package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface AnnotationDatasetReader<T extends Annotation> extends AutoCloseableNoThrow {
  /** {@return annotation data set, never <code>null</code>} */
  AnnotationDataset<T> read(PartitionKey partitionKey);
}
