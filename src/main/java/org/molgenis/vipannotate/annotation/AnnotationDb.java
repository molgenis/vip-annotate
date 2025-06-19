package org.molgenis.vipannotate.annotation;

public interface AnnotationDb<T extends Feature, U extends Annotation> extends AutoCloseable {
  U findAnnotations(T feature);

  void close();
}
