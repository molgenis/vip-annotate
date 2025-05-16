package org.molgenis.vcf.annotate.db.exact.format;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationDecoder<T> {
  T decode(MemoryBuffer memoryBuffer);
}
