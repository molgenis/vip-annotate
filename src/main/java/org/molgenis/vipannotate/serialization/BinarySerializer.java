package org.molgenis.vipannotate.serialization;

public interface BinarySerializer<T> {
  /** write object to a memory buffer */
  void writeTo(MemoryBuffer memoryBuffer, T object);

  /** read new object from a memory buffer */
  T readFrom(MemoryBuffer memoryBuffer);

  /** read memory buffer into an existing object */
  void readInto(MemoryBuffer memoryBuffer, T object);

  /** create a new default (empty) instance suitable for reuse with {@link #readInto}. */
  T readEmpty();
}
