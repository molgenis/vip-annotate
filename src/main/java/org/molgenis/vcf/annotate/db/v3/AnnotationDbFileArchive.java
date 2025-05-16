package org.molgenis.vcf.annotate.db.v3;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.Variant;

public interface AnnotationDbFileArchive<KeyType> extends AutoCloseable {
  CompositePartitionKey<KeyType> getPartition(Variant variant);

  MemoryBuffer getIndex(Variant variant);
}
