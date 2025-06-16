package org.molgenis.vipannotate.annotation;

import org.apache.fury.memory.MemoryBuffer;

public interface GenomePartitionDataWriter {
  void write(GenomePartitionKey genomePartitionKey, String dataId, MemoryBuffer memoryBuffer);
}
