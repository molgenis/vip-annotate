package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationBlobReader implements AutoCloseable {
  private final String blobId;
  private final PartitionDatasetReader partitionDatasetReader;
  private final MemorySegment memorySegment;

  /** {@return memory buffer or <code>null</code> if blob does not exist} */
  public @Nullable MemoryBuffer read(PartitionKey partitionKey) {
    return partitionDatasetReader.read(partitionKey, blobId, memorySegment);
  }

  @Override
  public void close() {}
}
