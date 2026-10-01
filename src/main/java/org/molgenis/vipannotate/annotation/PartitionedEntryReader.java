package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.PartitionedMemoryReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class PartitionedEntryReader implements AutoCloseableNoThrow {
  private final String entryId;
  private final PartitionedMemoryReader partitionReader;

  @Nullable private MemoryBuffer reusableMemBuffer;
  @Nullable private MemoryDecoder reusableMemDecoder;

  /**
   * {@return {@link MemoryDecoder} or <code>null</code> if blob does not exist}.
   *
   * <p>The returned {@link MemoryDecoder} is reused by subsequent calls and must not be retained
   * after another call.
   */
  public @Nullable MemoryDecoder read(PartitionKey partitionKey) {
    if (reusableMemBuffer == null) {
      reusableMemBuffer = partitionReader.read(partitionKey, entryId);

      if (reusableMemBuffer == null) {
        return null;
      }

      reusableMemDecoder = MemoryDecoder.wrap(reusableMemBuffer);
    } else {
      reusableMemBuffer.clear();
      if (!partitionReader.readInto(partitionKey, entryId, reusableMemBuffer)) {
        return null;
      }
    }
    return reusableMemDecoder;
  }

  @Override
  public void close() {
    ClosableUtils.close(reusableMemBuffer);
  }
}
