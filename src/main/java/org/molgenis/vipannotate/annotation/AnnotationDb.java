package org.molgenis.vipannotate.annotation;

public interface AnnotationDb<T> extends AutoCloseable {
  T findAnnotations(Variant variant);

  void close();
}
