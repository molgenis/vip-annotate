package org.molgenis.vipannotate.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class AutoGrowingMemoryEncoder implements MemoryEncoder {
  private final MemoryBuffer memBuffer;

  public static MemoryEncoder wrap(MemoryBuffer memBuffer) {
    return new AutoGrowingMemoryEncoder(memBuffer);
  }

  @Override
  public MemoryBuffer unwrap() {
    return memBuffer;
  }

  @Override
  public <T> void putArray(T[] array, MemoryEncoderFunction<T> elementEncoder) {
    putVarInt(array.length);
    for (T element : array) {
      elementEncoder.write(this, element);
    }
  }

  @Override
  public void putBoolean(boolean b) {
    memBuffer.putBoolean(b);
  }

  @Override
  public void putByte(byte value) {
    memBuffer.putByte(value);
  }

  @Override
  public void putByteAtIndex(long index, byte value) {
    memBuffer.putByteAtIndex(index, value);
  }

  @Override
  public void putByteArray(byte[] array) {
    putVarInt(array.length);
    memBuffer.putByteArray(array);
  }

  @Override
  public void putDouble(double d) {
    memBuffer.putDouble(d);
  }

  /** Writes a double at the given index. */
  @Override
  public void putDoubleAtIndex(long index, double value) {
    memBuffer.putDoubleAtIndex(index, value);
  }

  @Override
  public void putDoubleArray(double[] array) {
    putVarInt(array.length);
    memBuffer.putDoubleArray(array);
  }

  @SuppressWarnings("EnumOrdinal")
  @Override
  public <E extends Enum<E>> void putEnum(E enumConstant) {
    putVarInt(enumConstant.ordinal());
  }

  @SuppressWarnings("EnumOrdinal")
  @Override
  public <E extends Enum<E>> void putEnumSet(EnumSet<E> enumSet) {
    putVarInt(enumSet.size());
    for (E enumConstant : enumSet) {
      putVarInt(enumConstant.ordinal());
    }
  }

  @Override
  public void putFloat(float value) {
    memBuffer.putFloat(value);
  }

  /** Writes a float at the given index. */
  @Override
  public void putFloatAtIndex(long index, float value) {
    memBuffer.putFloatAtIndex(index, value);
  }

  @Override
  public void putFloatArray(float[] array) {
    putVarInt(array.length);
    memBuffer.putFloatArray(array);
  }

  @Override
  public void putInt(int i) {
    memBuffer.putInt(i);
  }

  @Override
  public void putIntAtIndex(long index, int value) {
    memBuffer.putIntAtIndex(index, value);
  }

  @Override
  public void putIntArray(int[] array) {
    putVarInt(array.length);
    memBuffer.putIntArray(array);
  }

  @Override
  public void putLong(long value) {
    memBuffer.putLong(value);
  }

  @Override
  public void putLongAtIndex(long index, long value) {
    memBuffer.putLongAtIndex(index, value);
  }

  @Override
  public void putLongArray(long[] array) {
    putVarInt(array.length);
    memBuffer.putLongArray(array);
  }

  @Override
  public <K, V> void putMap(
      Map<K, V> map, MemoryEncoderFunction<K> keyWriter, MemoryEncoderFunction<V> valueEncoder) {
    putVarInt(map.size());

    for (Map.Entry<K, V> entry : map.entrySet()) {
      keyWriter.write(this, entry.getKey());
      valueEncoder.write(this, entry.getValue());
    }
  }

  @Override
  public void putShort(short value) {
    memBuffer.putShort(value);
  }

  @Override
  public void putShortAtIndex(long index, short value) {
    memBuffer.putShortAtIndex(index, value);
  }

  @Override
  public void putShortArray(short[] array) {
    putVarInt(array.length);
    memBuffer.putShortArray(array);
  }

  @Override
  public void putString(String str) {
    byte[] bytes = str.getBytes(UTF_8);
    putVarInt(bytes.length);
    memBuffer.putByteArray(bytes);
  }

  @Override
  public void putMemoryEncoder(MemoryEncoder memEncoder) {
    MemoryBuffer source = memEncoder.unwrap();
    long byteLength = source.getLimit() - source.getPosition();

    putVarInt(Math.toIntExact(byteLength)); // FIXME need putVarLong?
    memBuffer.copyFrom(source, source.getPosition(), byteLength);
  }
}
