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
public interface MemoryEncoder {
  int VAR_INT_MAX_BYTE_SIZE = Integer.BYTES + 1;

  /** Returns an encoder that automatically grows the wrapped {@link MemoryBuffer}. */
  static MemoryEncoder autoGrowing(MemoryBuffer buffer) {
    return AutoGrowingMemoryEncoder.wrap(buffer);
  }

  /** Returns an encoder with the wrapped {@link MemoryBuffer}. */
  static MemoryEncoder fixed(MemoryBuffer buffer) {
    return FixedMemoryEncoder.wrap(buffer);
  }

  MemoryBuffer unwrap();

  <T> void putArray(T[] array, MemoryEncoderFunction<T> elementEncoder);

  void putBoolean(boolean b);

  void putByte(byte value);

  void putByteAtIndex(long index, byte value);

  void putByteArray(byte[] array);

  void putDouble(double d);

  void putDoubleAtIndex(long index, double value);

  default void putDoubleNullable(@Nullable Double d) {
    putBoolean(d != null);
    if (d != null) {
      putDouble(d);
    }
  }

  void putDoubleArray(double[] array);

  @SuppressWarnings("EnumOrdinal")
  <E extends Enum<E>> void putEnum(E enumConstant);

  @SuppressWarnings("EnumOrdinal")
  <E extends Enum<E>> void putEnumSet(EnumSet<E> enumSet);

  void putFloat(float value);

  void putFloatAtIndex(long index, float value);

  default void putFloatNullable(@Nullable Float f) {
    putBoolean(f != null);
    if (f != null) {
      putFloat(f);
    }
  }

  void putFloatArray(float[] array);

  void putInt(int i);

  void putIntAtIndex(long index, int value);

  default void putIntNullable(@Nullable Integer i) {
    putBoolean(i != null);
    if (i != null) {
      putInt(i);
    }
  }

  void putIntArray(int[] array);

  void putLong(long value);

  void putLongAtIndex(long index, long value);

  default void putLongNullable(@Nullable Long l) {
    putBoolean(l != null);
    if (l != null) {
      putLong(l);
    }
  }

  void putLongArray(long[] array);

  <K, V> void putMap(
      Map<K, V> map, MemoryEncoderFunction<K> keyWriter, MemoryEncoderFunction<V> valueEncoder);

  default <T> void putNullable(@Nullable T value, Consumer<T> valueEncoder) {
    putBoolean(value != null);
    if (value != null) {
      valueEncoder.accept(value);
    }
  }

  void putShort(short value);

  void putShortAtIndex(long index, short value);

  default void putShortNullable(@Nullable Short s) {
    putBoolean(s != null);
    if (s != null) {
      putShort(s);
    }
  }

  void putShortArray(short[] array);

  void putString(String str);

  default void putStringNullable(@Nullable String str) {
    putBoolean(str != null);
    if (str != null) {
      putString(str);
    }
  }

  default void putVarInt(int value) {
    // fast path: fits in 7 bits
    if ((value >>> 7) == 0) {
      putByte((byte) value);
      return;
    }

    // fits in 14 bits: write short
    long encoded = (value & 0x7F) | (((value & 0x3F80) << 1) | 0x80);
    if ((value >>> 14) == 0) {
      putShort((short) encoded);
      return;
    }

    // fits in 21 bits: write lower 2-bytes as short and upper 1-byte as byte
    encoded |= (((value & 0x1FC000) << 2) | 0x8000);
    if ((value >>> 21) == 0) {
      putShort((short) (encoded & 0xFFFF));
      putByte((byte) ((encoded >>> 16) & 0xFF));
      return;
    }

    // fits in 28 bits: write int
    encoded |= ((value & 0xFE00000L) << 3) | 0x800000L;
    if ((value >>> 28) == 0) {
      putInt((int) encoded);
      return;
    }

    // fits in 32 bits: write lower 4-bytes as int and upper 1-byte as byte
    encoded |= ((value & 0xFF0000000L) << 4) | 0x80000000L;
    putInt((int) (encoded & 0xFFFFFFFFL));
    putByte((byte) ((encoded >>> 32) & 0xFF));
  }

  void putMemoryEncoder(MemoryEncoder memEncoder);
}
