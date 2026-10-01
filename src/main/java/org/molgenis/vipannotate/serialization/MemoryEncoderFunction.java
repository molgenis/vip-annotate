package org.molgenis.vipannotate.serialization;

@FunctionalInterface
public interface MemoryEncoderFunction<T> {
  void write(MemoryEncoder writer, T value);
}
