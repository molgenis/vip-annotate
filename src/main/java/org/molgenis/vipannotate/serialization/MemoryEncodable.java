package org.molgenis.vipannotate.serialization;

public interface MemoryEncodable<T> {
  /** write object to a new {@link MemoryEncoder}. */
  MemoryEncoder writeTo(T object);

  /** write object to the given {@link MemoryEncoder}. */
  void writeInto(T object, MemoryEncoder memEncoder);
}
