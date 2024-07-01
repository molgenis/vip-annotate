package org.molgenis.vipannotate.annotation;

public interface AnnotationIndexReader<T extends Feature> extends AutoCloseable {
  AnnotationIndex<T> read(PartitionKey partitionKey);

  void readInto(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex);

  @Override
  void close();
}
