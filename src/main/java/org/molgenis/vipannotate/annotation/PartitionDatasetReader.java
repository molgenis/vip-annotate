package org.molgenis.vipannotate.annotation;

import java.nio.ByteBuffer;
import org.apache.fory.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;

public interface PartitionDatasetReader {
  @Nullable MemoryBuffer read(
      Partition.Key partitionKey, String datasetId, ByteBuffer directByteBuffer);
}
