package org.molgenis.vipannotate.db.v3;

import org.molgenis.vipannotate.db.exact.Variant;

public interface AnnotationDb<T> extends AutoCloseable {
  T findAnnotations(Variant variant);

  void close();
}
