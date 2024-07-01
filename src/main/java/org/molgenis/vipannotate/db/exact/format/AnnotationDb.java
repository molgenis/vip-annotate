package org.molgenis.vipannotate.db.exact.format;

import org.molgenis.vipannotate.db.exact.Variant;

public interface AnnotationDb<T> extends AutoCloseable {
  T findAnnotations(Variant variant);

  void close();
}
