package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class AnnotationBlobReader implements AutoCloseable {
  private final String blobId;
  private final BinaryPartitionReader partitionReader;
  @Nullable private MemoryBuffer reusableMemBuffer;

  /** {@return memory buffer or <code>null</code> if blob does not exist} */
  public @Nullable MemoryBuffer read(PartitionKey partitionKey) {
    if (reusableMemBuffer == null) {
      reusableMemBuffer = partitionReader.read(partitionKey, blobId);
    } else {
      reusableMemBuffer.clear();
      if (!partitionReader.readInto(partitionKey, blobId, reusableMemBuffer)) {
        return null;
      }
    }
    return reusableMemBuffer;
  }

  @Override
  public void close() {
    ClosableUtils.close(reusableMemBuffer);
  }
}
