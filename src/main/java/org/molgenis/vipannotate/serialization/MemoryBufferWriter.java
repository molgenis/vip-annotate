package org.molgenis.vipannotate.serialization;

public interface MemoryBufferWriter<T> {
  /** write object to a new {@link MemoryBuffer}. */
  MemoryBuffer writeTo(T object);

  /** write object to the given {@link MemoryBuffer}. */
  void writeInto(T object, MemoryBuffer memoryBuffer);
}
