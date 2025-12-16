package org.molgenis.vipannotate.serialization;

public interface MemoryBufferFactory {
  MemoryBuffer newMemoryBuffer();

  MemoryBuffer newMemoryBuffer(long minCapacity);
}
