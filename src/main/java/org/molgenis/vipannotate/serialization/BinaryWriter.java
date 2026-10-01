package org.molgenis.vipannotate.serialization;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/**
 * Writes binary data into a {@link MemoryBuffer}.
 *
 * <p>The caller is responsible for closing the buffer when it is no longer needed.
 */
public interface BinaryWriter {
  /** Returns a writer that automatically grows the wrapped {@link MemoryBuffer}. */
  static BinaryWriter autoGrowing(MemoryBuffer buffer) {
    return new AutoGrowingBinaryWriter(buffer);
  }

  /** Returns a writer with the wrapped {@link MemoryBuffer}. */
  static BinaryWriter fixed(MemoryBuffer buffer) {
    return new FixedBinaryWriter(buffer);
  }

  <T> void writeArray(T[] array, BinaryWriterFunction<T> elementWriter);

  void writeBoolean(boolean b);

  void writeByte(byte value);

  void writeByteAtIndex(long index, byte value);

  void writeDouble(double d);

  void writeDoubleAtIndex(long index, double value);

  default void writeDoubleNullable(@Nullable Double d) {
    writeBoolean(d != null);
    if (d != null) {
      writeDouble(d);
    }
  }

  @SuppressWarnings("EnumOrdinal")
  <E extends Enum<E>> void writeEnum(E enumConstant);

  @SuppressWarnings("EnumOrdinal")
  <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumSet);

  void writeFloat(float value);

  void writeFloatAtIndex(long index, float value);

  default void writeFloatNullable(@Nullable Float f) {
    writeBoolean(f != null);
    if (f != null) {
      writeFloat(f);
    }
  }

  void writeInt(int i);

  void writeIntAtIndex(long index, int value);

  default void writeIntNullable(@Nullable Integer i) {
    writeBoolean(i != null);
    if (i != null) {
      writeInt(i);
    }
  }

  void writeLong(long value);

  void writeLongAtIndex(long index, long value);

  default void writeLongNullable(@Nullable Long l) {
    writeBoolean(l != null);
    if (l != null) {
      writeLong(l);
    }
  }

  <K, V> void writeMap(
      Map<K, V> map, BinaryWriterFunction<K> keyWriter, BinaryWriterFunction<V> valueWriter);

  default <T> void writeNullable(@Nullable T value, Consumer<T> valueWriter) {
    writeBoolean(value != null);
    if (value != null) {
      valueWriter.accept(value);
    }
  }

  void writeShort(short value);

  void writeShortAtIndex(long index, short value);

  default void writeShortNullable(@Nullable Short s) {
    writeBoolean(s != null);
    if (s != null) {
      writeShort(s);
    }
  }

  void writeString(String str);

  default void writeStringNullable(@Nullable String str) {
    writeBoolean(str != null);
    if (str != null) {
      writeString(str);
    }
  }
}
