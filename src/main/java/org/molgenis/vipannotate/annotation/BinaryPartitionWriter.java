package org.molgenis.vipannotate.annotation;

import org.apache.fory.memory.MemoryBuffer;

public interface BinaryPartitionWriter {
  void write(Partition.Key partitionKey, String dataId, MemoryBuffer memoryBuffer);
}
