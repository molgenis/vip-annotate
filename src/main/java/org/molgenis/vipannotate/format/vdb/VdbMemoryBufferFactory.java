package org.molgenis.vipannotate.format.vdb;

import static org.molgenis.vipannotate.format.vdb.VdbArchive.VDB_BYTE_ALIGNMENT;

import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;

public class VdbMemoryBufferFactory implements MemoryBufferFactory {
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
  private static long alignedLength(long length) {
    return (length + (VDB_BYTE_ALIGNMENT - 1)) & -VDB_BYTE_ALIGNMENT;
  }
}
