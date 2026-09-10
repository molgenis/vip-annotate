package org.molgenis.vipannotate.serialization;

@FunctionalInterface
public interface BinaryReaderFunction<T> {
  T read(BinaryReader reader);
}
