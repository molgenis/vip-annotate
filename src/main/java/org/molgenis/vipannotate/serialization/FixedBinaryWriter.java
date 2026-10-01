package org.molgenis.vipannotate.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class FixedBinaryWriter implements BinaryWriter {
  private final MemoryBuffer memBuffer;

  @Override
  public <T> void writeArray(T[] array, BinaryWriterFunction<T> elementWriter) {
    memBuffer.putVarUnsignedIntUnchecked(array.length);
    for (T element : array) {
      elementWriter.write(this, element);
    }
  }

  @Override
  public void writeBoolean(boolean b) {
    memBuffer.putBooleanUnchecked(b);
  }

  @Override
  public void writeByte(byte value) {
    memBuffer.putByteUnchecked(value);
  }

  @Override
  public void writeByteAtIndex(long index, byte value) {
    memBuffer.setByteAtIndexUnchecked(index, value);
  }

  @Override
  public void writeDouble(double d) {
    memBuffer.putDoubleUnchecked(d);
  }

  /** Writes a double at the given index. */
  @Override
  public void writeDoubleAtIndex(long index, double value) {
    memBuffer.setDoubleAtIndexUnchecked(index, value);
  }

  @SuppressWarnings("EnumOrdinal")
  @Override
  public <E extends Enum<E>> void writeEnum(E enumConstant) {
    memBuffer.putVarUnsignedIntUnchecked(enumConstant.ordinal());
  }

  @SuppressWarnings("EnumOrdinal")
  @Override
  public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumSet) {
    memBuffer.putVarUnsignedIntUnchecked(enumSet.size());
    for (E enumConstant : enumSet) {
      memBuffer.putVarUnsignedIntUnchecked(enumConstant.ordinal());
    }
  }

  @Override
  public void writeFloat(float value) {
    memBuffer.putFloatUnchecked(value);
  }

  /** Writes a float at the given index. */
  @Override
  public void writeFloatAtIndex(long index, float value) {
    memBuffer.setFloatAtIndexUnchecked(index, value);
  }

  @Override
  public void writeInt(int i) {
    memBuffer.putIntUnchecked(i);
  }

  @Override
  public void writeIntAtIndex(long index, int value) {
    memBuffer.setIntAtIndexUnchecked(index, value);
  }

  @Override
  public void writeLong(long value) {
    memBuffer.putLongUnchecked(value);
  }

  @Override
  public void writeLongAtIndex(long index, long value) {
    memBuffer.setLongAtIndexUnchecked(index, value);
  }

  @Override
  public <K, V> void writeMap(
      Map<K, V> map, BinaryWriterFunction<K> keyWriter, BinaryWriterFunction<V> valueWriter) {
    memBuffer.putVarUnsignedIntUnchecked(map.size());

    for (Map.Entry<K, V> entry : map.entrySet()) {
      keyWriter.write(this, entry.getKey());
      valueWriter.write(this, entry.getValue());
    }
  }

  @Override
  public void writeShort(short value) {
    memBuffer.putShortUnchecked(value);
  }

  @Override
  public void writeShortAtIndex(long index, short value) {
    memBuffer.setShortAtIndexUnchecked(index, value);
  }

  @Override
  public void writeString(String str) {
    memBuffer.putByteArrayUnchecked(str.getBytes(UTF_8));
  }
}
