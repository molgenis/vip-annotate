package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.SizedIterable;

public interface AnnotationDatasetWriter<T> {
  void write(GenomePartitionKey genomePartitionKey, SizedIterable<T> annotations);
}
