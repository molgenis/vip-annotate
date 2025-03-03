package org.molgenis.vcf.annotate.db2.exact.format;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationDb extends AutoCloseable {
  MemoryBuffer findVariant(String contig, int start, int stop, byte[] altBases);
}
