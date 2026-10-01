package org.molgenis.vipannotate.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor
public final class BinaryReader implements AutoCloseableNoThrow {
  private final MemoryBuffer memBuffer;

  public <T> T[] readArray(IntFunction<T[]> arrayFactory, BinaryReaderFunction<T> elementReader) {
    int length = memBuffer.getVarUnsignedInt();
    T[] array = arrayFactory.apply(length);

    for (int i = 0; i < length; i++) {
      array[i] = elementReader.read(this);
    }

    return array;
  }

  public boolean readBoolean() {
    return memBuffer.getBoolean();
  }

  /** Returns the byte at the current position and increments the position */
  public byte getByte() {
    return memBuffer.getByte();
  }

  /** Returns the byte at the given index */
  public byte getByteAtIndex(long index) {
    return memBuffer.getByteAtIndex(index);
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

  // FIXME pos same as index?
  /** Returns the unsigned byte at the given position */
  public int getUnsignedByte(long pos) {
    return Byte.toUnsignedInt(memBuffer.getByte(pos));
  }

  /** Returns the float at the given index. */
  public float getFloatAtIndex(long index) {
    return memBuffer.getFloatAtIndex(index);
  }

  /** same as {@link #getUnsignedByte()} for <code>int</code>. */
  public long getUnsignedInt() {
    return Integer.toUnsignedLong(memBuffer.getInt());
  }

  /** same as {@link #getUnsignedByte(long)} for <code>int</code>. */
  public long getUnsignedInt(long pos) {
    return Integer.toUnsignedLong(memBuffer.getIntAtIndex(pos));
  }

  /** same as {@link #getByteAtIndex(long)} for <code>int</code>. */
  public int getIntAtIndex(long index) {
    return memBuffer.getIntAtIndex(index);
  }

  /** same as {@link #getByteAtIndex(long)} for <code>int</code>. */
  public long getUnsignedIntAtIndex(long index) {
    return Integer.toUnsignedLong(getIntAtIndex(index));
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

  /** same as {@link #getUnsignedByte()} for <code>short</code>. */
  public int getUnsignedShort() {
    return Short.toUnsignedInt(getShort());
  }

  /** see {@link #getUnsignedByteAtIndex(long)} for <code>short</code>. */
  public int getUnsignedShortAtIndex(long index) {
    return Short.toUnsignedInt(getShortAtIndex(index));
  }

  public double readDouble() {
    return memBuffer.getDouble();
  }

  public <E extends Enum<E>> E readEnum(Class<E> enumClass) {
    return enumClass.getEnumConstants()[memBuffer.getVarUnsignedInt()];
  }

  public <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> enumClass) {
    int size = memBuffer.getVarUnsignedInt();
    EnumSet<E> enumSet = EnumSet.noneOf(enumClass);

    E[] enumConstants = enumClass.getEnumConstants();
    for (int i = 0; i < size; i++) {
      enumSet.add(enumConstants[memBuffer.getVarUnsignedInt()]);
    }

    return enumSet;
  }

  public int readInteger() {
    return memBuffer.getInt();
  }

  public @Nullable Integer readIntegerNullable() {
    boolean b = readBoolean();
    return b ? readInteger() : null;
  }

  public <K, V> Map<K, V> readMap(
      BinaryReaderFunction<K> keyReader, BinaryReaderFunction<V> valueReader) {
    int size = memBuffer.getVarUnsignedInt();
    Map<K, V> map = Maps.newLinkedHashMapWithExpectedSize(size);

    for (int i = 0; i < size; i++) {
      K key = keyReader.read(this);
      V value = valueReader.read(this);
      map.put(key, value);
    }

    return map;
  }

  public <T> @Nullable T readNullable(Supplier<T> valueReader) {
    return readBoolean() ? valueReader.get() : null;
  }

  public String readString() {
    return new String(memBuffer.getByteArray(), UTF_8);
  }

  public @Nullable String readStringNullable() {
    boolean b = readBoolean();
    return b ? readString() : null;
  }

  public MemoryBuffer unwrap() {
    return memBuffer;
  }

  @Override
  public void close() {
    ClosableUtils.close(memBuffer);
  }
}
