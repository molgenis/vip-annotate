package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface BinaryPartitionWriter extends AutoCloseable {
  void write(PartitionKey partitionKey, String dataId, MemoryBuffer memoryBuffer);
}
