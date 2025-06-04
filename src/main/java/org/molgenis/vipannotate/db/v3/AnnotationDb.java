package org.molgenis.vipannotate.db.v3;

import java.io.IOException;
import org.molgenis.vipannotate.db.exact.Variant;

public interface AnnotationDb<T> extends AutoCloseable {
  T findAnnotations(Variant variant) throws IOException;
}
