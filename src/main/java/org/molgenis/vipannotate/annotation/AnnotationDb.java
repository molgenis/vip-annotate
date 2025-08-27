package org.molgenis.vipannotate.annotation;

import java.util.List;

public interface AnnotationDb<T extends Feature, U extends Annotation> extends AutoCloseable {
  List<U> findAnnotations(T feature);

  void close();
}
