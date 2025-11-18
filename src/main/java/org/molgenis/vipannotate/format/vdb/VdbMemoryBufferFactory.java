package org.molgenis.vipannotate.format.vdb;

import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;

public class VdbMemoryBufferFactory implements MemoryBufferFactory {
  /**
   * archive data is aligned to 4096 to allow for {@link
   * com.sun.nio.file.ExtendedOpenOption#DIRECT}.
   */
  private static final int VDB_BYTE_ALIGNMENT = 4096;

  private static final long DEFAULT_MIN_CAPACITY = 1 << 20; // 1 MB

  @Override
  public MemoryBuffer newMemoryBuffer() {
    return newMemoryBuffer(DEFAULT_MIN_CAPACITY);
  }

  /**
   * Create new memory buffer with that meets vdb alignment requirements. The capacity of the memory
   * buffer is guaranteed to be >= the requested capacity.
   */
  @Override
  public MemoryBuffer newMemoryBuffer(long minCapacity) {
    long alignedLength = alignedLength(minCapacity);
    return MemoryBuffer.allocate(alignedLength, VDB_BYTE_ALIGNMENT);
  }

  /** find next alignment multiple */
  static long alignedLength(long length) {
    return (length + (VDB_BYTE_ALIGNMENT - 1)) & -VDB_BYTE_ALIGNMENT;
  }

  /** check whether the given memory buffer meets vdb alignment requirements */
  static boolean isAligned(MemoryBuffer memBuffer) {
    return memBuffer.getAlignment() == VDB_BYTE_ALIGNMENT
        && memBuffer.getPosition() % VDB_BYTE_ALIGNMENT == 0
        && memBuffer.getLimit() % VDB_BYTE_ALIGNMENT == 0;
  }
}
