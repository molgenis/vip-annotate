package org.molgenis.vipannotate.serialization;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.Numbers;

public final class ScratchMemoryBuffer implements AutoCloseableNoThrow {
  @Nullable private MemoryBuffer scratchBuffer;

  public MemoryBuffer get(long minCapacity) {
    if (scratchBuffer == null) {
      scratchBuffer = MemoryBuffer.wrap(new byte[Math.toIntExact(minCapacity)]);
    } else {
      if (minCapacity > scratchBuffer.getCapacity()) {
        // ensureCapacity does not support heap backed buffers, create a new one
        scratchBuffer.close();
        scratchBuffer =
            MemoryBuffer.wrap(new byte[Math.toIntExact(Numbers.nextPowerOf2(minCapacity))]);
      } else {
        scratchBuffer.clear();
      }
    }
    return scratchBuffer;
  }

  @Override
  public void close() {
    if (scratchBuffer != null) {
      scratchBuffer.close();
      scratchBuffer = null;
    }
  }
}
