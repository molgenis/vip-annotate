package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface AnnotationDatasetDecoder<T extends Annotation> extends AutoCloseableNoThrow {
  /** {@return annotation data set, never <code>null</code>} */
  AnnotationDataset<T> decode(PartitionKey partitionKey);
}
