package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public interface AnnotationDb<T extends Feature, U extends Annotation> extends AutoCloseable {
  @Nullable U findAnnotations(T feature);

  void close();
}
