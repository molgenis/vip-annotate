package org.molgenis.vipannotate.serialization;

public interface MemoryDecodable<T> {
  /** decode new object from memory */
  T readFrom(MemoryDecoder memDecoder);

  /** decode memory into an existing object */
  void readInto(MemoryDecoder memDecoder, T object);
}
