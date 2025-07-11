package org.molgenis.vipannotate.annotation;

public interface AnnotationIndexReader<T extends Feature> extends AutoCloseable {
  AnnotationIndex<T> read(Partition.Key partitionKey);

  @Override
  void close();
}
