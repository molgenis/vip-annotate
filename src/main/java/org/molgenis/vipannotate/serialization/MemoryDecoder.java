package org.molgenis.vipannotate.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemoryDecoder {
  private final MemoryBuffer memBuffer;

  public static MemoryDecoder wrap(MemoryBuffer memBuffer) {
    return new MemoryDecoder(memBuffer);
  }

  public MemoryBuffer unwrap() {
    return memBuffer;
  }

  public <T> T[] getArray(IntFunction<T[]> arrayFactory, MemoryDecoderFunction<T> elementDecoder) {
    int length = getVarInt();
    T[] array = arrayFactory.apply(length);

    for (int i = 0; i < length; i++) {
      array[i] = elementDecoder.read(this);
    }

    return array;
  }

  public boolean getBoolean() {
    return memBuffer.getBoolean();
  }

  /** Returns the next byte */
  public byte getByte() {
    return memBuffer.getByte();
  }

  /** Returns the byte at the given index */
  public byte getByteAtIndex(long index) {
    return memBuffer.getByteAtIndex(index);
  }

  /** Returns the next {@link Byte} */
  public @Nullable Byte getByteNullable() {
    boolean b = getBoolean();
    return b ? getByte() : null;
  }

  /** Returns the next <code>byte[]</code> */
  public byte[] getByteArray() {
    int arrayLength = getVarInt();
    return memBuffer.getByteArray(arrayLength);
  }

  /** Returns the double at the given index. */
  public double getDoubleAtIndex(long index) {
    return memBuffer.getDoubleAtIndex(index);
  }

  /** Returns the unsigned byte at the current position and increments the position */
  public int getUnsignedByte() {
    return Byte.toUnsignedInt(memBuffer.getByte());
  }

  /** Returns the byte at the given index */
  public int getUnsignedByteAtIndex(long index) {
    return Byte.toUnsignedInt(memBuffer.getByteAtIndex(index));
  }

  public float getFloat() {
    return memBuffer.getFloat();
  }

  /** Returns the float at the given index. */
  public float getFloatAtIndex(long index) {
    return memBuffer.getFloatAtIndex(index);
  }

  public @Nullable Float getFloatNullable() {
    boolean b = getBoolean();
    return b ? getFloat() : null;
  }

  /** see {@link #getByteArray()} for <code>float[]</code>. */
  public float[] getFloatArray() {
    int arrayLength = getVarInt();
    return memBuffer.getFloatArray(arrayLength);
  }

  /** same as {@link #getUnsignedByte()} for <code>int</code>. */
  public long getUnsignedInt() {
    return Integer.toUnsignedLong(memBuffer.getInt());
  }

  /** same as {@link #getUnsignedByteAtIndex(long)} for <code>int</code>. */
  public long getUnsignedIntAtIndex(long pos) {
    return Integer.toUnsignedLong(memBuffer.getIntAtIndex(pos));
  }

  /** same as {@link #getByteAtIndex(long)} for <code>int</code>. */
  public int getIntAtIndex(long index) {
    return memBuffer.getIntAtIndex(index);
  }

  /** same as {@link #getByteAtIndex(long)} for <code>long</code>. */
  public long getLongAtIndex(long index) {
    return memBuffer.getLongAtIndex(index);
  }

  /** same as {@link #getByte()} for <code>short</code>. */
  public short getShort() {
    return memBuffer.getShort();
  }

  /** see {@link #getByteAtIndex(long)} for <code>short</code>. */
  public short getShortAtIndex(long index) {
    return memBuffer.getShortAtIndex(index);
  }

  public @Nullable Short getShortNullable() {
    boolean b = getBoolean();
    return b ? getShort() : null;
  }

  /** see {@link #getByteArray()} for <code>short[]</code>. */
  public short[] getShortArray() {
    int arrayLength = getVarInt();
    return memBuffer.getShortArray(arrayLength);
  }

  /** same as {@link #getUnsignedByte()} for <code>short</code>. */
  public int getUnsignedShort() {
    return Short.toUnsignedInt(getShort());
  }

  /** see {@link #getUnsignedByteAtIndex(long)} for <code>short</code>. */
  public int getUnsignedShortAtIndex(long index) {
    return Short.toUnsignedInt(getShortAtIndex(index));
  }

  public double getDouble() {
    return memBuffer.getDouble();
  }

  public @Nullable Double getDoubleNullable() {
    boolean b = getBoolean();
    return b ? getDouble() : null;
  }

  /** see {@link #getByteArray()} for <code>double[]</code>. */
  public double[] getDoubleArray() {
    int arrayLength = getVarInt();
    return memBuffer.getDoubleArray(arrayLength);
  }

  public <E extends Enum<E>> E getEnum(Class<E> enumClass) {
    return enumClass.getEnumConstants()[getVarInt()];
  }

  public <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> enumClass) {
    int size = getVarInt();
    EnumSet<E> enumSet = EnumSet.noneOf(enumClass);

    E[] enumConstants = enumClass.getEnumConstants();
    for (int i = 0; i < size; i++) {
      enumSet.add(enumConstants[getVarInt()]);
    }

    return enumSet;
  }

  public int getInt() {
    return memBuffer.getInt();
  }

  public @Nullable Integer getIntNullable() {
    boolean b = getBoolean();
    return b ? getInt() : null;
  }

  /** see {@link #getByteArray()} for <code>int[]</code>. */
  public int[] getIntArray() {
    int arrayLength = getVarInt();
    return memBuffer.getIntArray(arrayLength);
  }

  public <K, V> Map<K, V> getMap(
      MemoryDecoderFunction<K> keyReader, MemoryDecoderFunction<V> valueReader) {
    int size = getVarInt();
    Map<K, V> map = Maps.newLinkedHashMapWithExpectedSize(size);
    getMapWithSizeInto(map, size, keyReader, valueReader);
    return map;
  }

  public <K, V> void getMapInto(
      Map<K, V> map, MemoryDecoderFunction<K> keyReader, MemoryDecoderFunction<V> valueReader) {
    int size = getVarInt();
    getMapWithSizeInto(map, size, keyReader, valueReader);
  }

  private <K, V> void getMapWithSizeInto(
      Map<K, V> map,
      int size,
      MemoryDecoderFunction<K> keyReader,
      MemoryDecoderFunction<V> valueReader) {
    for (int i = 0; i < size; i++) {
      K key = keyReader.read(this);
      V value = valueReader.read(this);
      map.put(key, value);
    }
  }

  public long getLong() {
    return memBuffer.getLong();
  }

  public @Nullable Long getLongNullable() {
    boolean b = getBoolean();
    return b ? getLong() : null;
  }

  /** see {@link #getByteArray()} for <code>long[]</code>. */
  public long[] getLongArray() {
    int arrayLength = getVarInt();
    return memBuffer.getLongArray(arrayLength);
  }

  public <T> @Nullable T getNullable(Supplier<T> valueReader) {
    return getBoolean() ? valueReader.get() : null;
  }

  public String getString() {
    int length = getVarInt();
    return new String(memBuffer.getByteArray(length), UTF_8);
  }

  public @Nullable String getStringNullable() {
    boolean b = getBoolean();
    return b ? getString() : null;
  }

  /**
   * Reads a 1–5 byte unsigned integer written with {@link MemoryEncoder#putVarInt}
   *
   * @return decoded integer
   */
  public int getVarInt() {
    int b0 = getUnsignedByte();

    if ((b0 & 0x80) == 0) {
      return b0;
    }

    int b1 = getUnsignedByte();
    int result = (b0 & 0x7F) | ((b1 & 0x7F) << 7);
    if ((b1 & 0x80) == 0) {
      return result;
    }

    int b2 = getUnsignedByte();
    result |= (b2 & 0x7F) << 14;
    if ((b2 & 0x80) == 0) {
      return result;
    }

    int b3 = getUnsignedByte();
    result |= (b3 & 0x7F) << 21;
    if ((b3 & 0x80) == 0) {
      return result;
    }

    int b4 = getUnsignedByte();
    result |= (b4 & 0x0F) << 28;
    return result;
  }

  /**
   * Reads a 1–10 byte unsigned variable-length integer written with {@link
   * MemoryEncoder#putVarLong(long)}.
   *
   * @return decoded long
   */
  public long getVarLong() {
    long result = 0;

    for (int shift = 0; shift < 64; shift += 7) {
      int b = getUnsignedByte();

      // tenth byte can contain only one payload bit.
      if (shift == 63) {
        if ((b & 0xFE) != 0) {
          throw new IllegalArgumentException();
        }
        return result | ((long) b << 63);
      }

      result |= (long) (b & 0x7F) << shift;

      if ((b & 0x80) == 0) {
        return result;
      }
    }

    throw new IllegalArgumentException();
  }

  /// Returns a {@link MemoryDecoder} for the next block of data.
  ///
  /// <p>The decoder shares the underlying memory without copying it.
  public MemoryDecoder getMemoryDecoder() {
    long byteLength = getVarLong();
    return MemoryDecoder.wrap(memBuffer.getMemoryBuffer(byteLength));
  }
}
