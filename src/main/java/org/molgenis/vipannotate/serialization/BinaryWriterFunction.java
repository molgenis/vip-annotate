package org.molgenis.vipannotate.serialization;

@FunctionalInterface
public interface BinaryWriterFunction<T> {
  void write(BinaryWriter writer, T value);
}
