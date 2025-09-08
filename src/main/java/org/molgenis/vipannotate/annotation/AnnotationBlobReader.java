package org.molgenis.vipannotate.annotation;

import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class AnnotationBlobReader implements AutoCloseable {
  private final String blobId;
  private final PartitionDatasetReader partitionDatasetReader;

  // make NonNull to include in constructor and allow setting to null in close
  @SuppressWarnings("NullableProblems")
  @lombok.NonNull
  private ByteBuffer reusableDirectByteBuffer;

  /**
   * @return memory buffer or <code>null</code> if blob does not exist
   */
  public @Nullable MemoryBuffer read(Partition.Key partitionKey) {
    return partitionDatasetReader.read(partitionKey, blobId, reusableDirectByteBuffer);
  }

  @Override
  public void close() {
    //noinspection DataFlowIssue
    reusableDirectByteBuffer = null; // make available for deallocation
  }
}
