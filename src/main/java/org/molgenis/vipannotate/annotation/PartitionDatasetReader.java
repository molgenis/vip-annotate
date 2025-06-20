package org.molgenis.vipannotate.annotation;

import java.nio.ByteBuffer;
import org.apache.fury.memory.MemoryBuffer;

public interface PartitionDatasetReader {
  MemoryBuffer read(Partition.Key partitionKey, String datasetId, ByteBuffer directByteBuffer);
}
