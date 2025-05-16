package org.molgenis.vcf.annotate.db.exact.format;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationEncoder<T> {
  MemoryBuffer encode(T annotation);
}
