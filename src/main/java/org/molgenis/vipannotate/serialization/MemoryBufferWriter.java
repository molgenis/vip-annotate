package org.molgenis.vipannotate.serialization;

// FIXME most comms must go through BinaryWriter
public interface MemoryBufferWriter<T> {
  /** write object to a new {@link MemoryBuffer}. */
  MemoryBuffer writeTo(T object);

  /** write object to the given {@link MemoryBuffer}. */
  void writeInto(T object, MemoryBuffer memoryBuffer);
}
