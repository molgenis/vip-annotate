package org.molgenis.vipannotate.db.v2;

import java.nio.ByteBuffer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationBlobReader implements AutoCloseable {
  @NonNull private final String blobId;
  @NonNull private final ZipZstdDecompressionContext decompressCtx;
  @NonNull private ByteBuffer reusableDirectByteBuffer;

  /**
   * @return memory buffer or <code>null</code> if blob does not exist
   */
  public MemoryBuffer read(GenomePartitionKey genomePartitionKey) {
    return decompressCtx.read(genomePartitionKey, blobId, reusableDirectByteBuffer);
  }

  @Override
  public void close() {
    //noinspection DataFlowIssue
    reusableDirectByteBuffer = null; // make available for deallocation
  }
}
