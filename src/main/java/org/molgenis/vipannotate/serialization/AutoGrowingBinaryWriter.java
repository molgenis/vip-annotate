package org.molgenis.vipannotate.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class AutoGrowingBinaryWriter implements BinaryWriter {
  private final MemoryBuffer memBuffer;

  @Override
  public <T> void writeArray(T[] array, BinaryWriterFunction<T> elementWriter) {
    memBuffer.putVarUnsignedInt(array.length);
    for (T element : array) {
      elementWriter.write(this, element);
    }
  }

  @Override
  public void writeBoolean(boolean b) {
    memBuffer.putBoolean(b);
  }

  @Override
  public void writeByte(byte value) {
    memBuffer.putByte(value);
  }

  @Override
  public void writeByteAtIndex(long index, byte value) {
    memBuffer.setByteAtIndex(index, value);
  }

  @Override
  public void writeDouble(double d) {
    memBuffer.putDouble(d);
  }

  /** Writes a double at the given index. */
  @Override
  public void writeDoubleAtIndex(long index, double value) {
    memBuffer.setDoubleAtIndex(index, value);
  }

  @SuppressWarnings("EnumOrdinal")
  @Override
  public <E extends Enum<E>> void writeEnum(E enumConstant) {
    memBuffer.putVarUnsignedInt(enumConstant.ordinal());
  }

  @SuppressWarnings("EnumOrdinal")
  @Override
  public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumSet) {
    memBuffer.putVarUnsignedInt(enumSet.size());
    for (E enumConstant : enumSet) {
      memBuffer.putVarUnsignedInt(enumConstant.ordinal());
    }
  }

  @Override
  public void writeFloat(float value) {
    memBuffer.putFloat(value);
  }

  /** Writes a float at the given index. */
  @Override
  public void writeFloatAtIndex(long index, float value) {
    memBuffer.setFloatAtIndex(index, value);
  }

  @Override
  public void writeInt(int i) {
    memBuffer.putInt(i);
  }

  @Override
  public void writeIntAtIndex(long index, int value) {
    memBuffer.setIntAtIndex(index, value);
  }

  @Override
  public void writeLong(long value) {
    memBuffer.putLong(value);
  }

  @Override
  public void writeLongAtIndex(long index, long value) {
    memBuffer.setLongAtIndex(index, value);
  }

  @Override
  public <K, V> void writeMap(
      Map<K, V> map, BinaryWriterFunction<K> keyWriter, BinaryWriterFunction<V> valueWriter) {
    memBuffer.putVarUnsignedInt(map.size());

    for (Map.Entry<K, V> entry : map.entrySet()) {
      keyWriter.write(this, entry.getKey());
      valueWriter.write(this, entry.getValue());
    }
  }

  @Override
  public void writeShort(short value) {
    memBuffer.putShort(value);
  }

  @Override
  public void writeShortAtIndex(long index, short value) {
    memBuffer.setShortAtIndex(index, value);
  }

  @Override
  public void writeString(String str) {
    memBuffer.putByteArray(str.getBytes(UTF_8));
  }
}
