package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface PartitionDatasetReader {
  @Nullable MemoryBuffer read(
      PartitionKey partitionKey, String datasetId, MemorySegment memorySegment);
}
