package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionReader;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class AnnotationBlobReader implements AutoCloseableNoThrow {
  private final String blobId;
  private final BinaryPartitionReader partitionReader;

  @Nullable private MemoryBuffer reusableMemBuffer;
  @Nullable private BinaryReader reusableBinaryReader;

  /**
   * {@return {@link BinaryReader} or <code>null</code> if blob does not exist}.
   *
   * <p>The returned BinaryReader is reused by subsequent calls and must not be retained after
   * another call.
   */
  public @Nullable BinaryReader read(PartitionKey partitionKey) {
    if (reusableMemBuffer == null) {
      reusableMemBuffer = partitionReader.read(partitionKey, blobId);

      if (reusableMemBuffer == null) {
        return null;
      }

      reusableBinaryReader = new BinaryReader(reusableMemBuffer);
    } else {
      reusableMemBuffer.clear();
      if (!partitionReader.readInto(partitionKey, blobId, reusableMemBuffer)) {
        return null;
      }
    }
    return reusableBinaryReader;
  }

  @Override
  public void close() {
    ClosableUtils.close(reusableBinaryReader);
  }
}
