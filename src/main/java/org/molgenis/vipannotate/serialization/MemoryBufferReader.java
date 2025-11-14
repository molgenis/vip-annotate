package org.molgenis.vipannotate.serialization;

public interface MemoryBufferReader<T> {
  /** read new object from a memory buffer */
  T readFrom(MemoryBuffer memoryBuffer);

  /** read memory buffer into an existing object */
  void readInto(MemoryBuffer memoryBuffer, T object);
}
