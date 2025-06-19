package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;

public interface BinaryPartitionWriter {
  void write(Partition.Key partitionKey, String dataId, MemoryBuffer memoryBuffer);
}
