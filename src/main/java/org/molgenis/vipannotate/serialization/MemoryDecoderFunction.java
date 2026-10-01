package org.molgenis.vipannotate.serialization;

@FunctionalInterface
public interface MemoryDecoderFunction<T> {
  T read(MemoryDecoder reader);
}
