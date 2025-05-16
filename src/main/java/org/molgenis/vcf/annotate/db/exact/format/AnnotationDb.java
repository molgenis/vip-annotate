package org.molgenis.vcf.annotate.db.exact.format;

import org.molgenis.vcf.annotate.db.exact.Variant;

public interface AnnotationDb<T> extends AutoCloseable {
  T findAnnotations(Variant variant);
}
