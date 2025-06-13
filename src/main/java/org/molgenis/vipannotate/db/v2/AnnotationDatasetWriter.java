package org.molgenis.vipannotate.db.v2;

import org.molgenis.vipannotate.util.SizedIterable;

public interface AnnotationDatasetWriter<T> {
  void write(GenomePartitionKey genomePartitionKey, SizedIterable<T> annotations);
}
