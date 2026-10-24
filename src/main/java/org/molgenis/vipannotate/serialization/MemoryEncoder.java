package org.molgenis.vipannotate.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemoryEncoder {
  public static final int VAR_INT_MAX_BYTE_SIZE = Integer.BYTES + 1;

  private final MemoryBuffer memBuffer;

  public static MemoryEncoder wrap(MemoryBuffer memBuffer) {
    return new MemoryEncoder(memBuffer);
  }

  public static MemoryEncoder wrap(byte[] bytes) {
    return new MemoryEncoder(MemoryBuffer.wrap(bytes));
  }

  public MemoryBuffer unwrap() {
    return memBuffer;
  }

  public <T> void putArray(T[] array, MemoryEncoderFunction<T> elementEncoder) {
    putVarInt(array.length);
    for (T element : array) {
      elementEncoder.write(this, element);
    }
  }

  public void putBoolean(boolean b) {
    memBuffer.putBoolean(b);
  }

  public void putByte(byte value) {
    memBuffer.putByte(value);
  }

  public void putByteArray(byte[] array) {
    putVarInt(array.length);
    memBuffer.putByteArray(array);
  }

  public void putDouble(double d) {
    memBuffer.putDouble(d);
  }

  public void putDoubleArray(double[] array) {
    putVarInt(array.length);
    memBuffer.putDoubleArray(array);
  }

  public void putDoubleNullable(@Nullable Double d) {
    putBoolean(d != null);
    if (d != null) {
      putDouble(d);
    }
  }

  @SuppressWarnings("EnumOrdinal")
  public <E extends Enum<E>> void putEnum(E enumConstant) {
    putVarInt(enumConstant.ordinal());
  }

  @SuppressWarnings("EnumOrdinal")
  public <E extends Enum<E>> void putEnumSet(EnumSet<E> enumSet) {
    putVarInt(enumSet.size());
    for (E enumConstant : enumSet) {
      putVarInt(enumConstant.ordinal());
    }
  }

  public void putFloat(float value) {
    memBuffer.putFloat(value);
  }

  public void putFloatArray(float[] array) {
    putVarInt(array.length);
    memBuffer.putFloatArray(array);
  }

  public void putFloatNullable(@Nullable Float f) {
    putBoolean(f != null);
    if (f != null) {
      putFloat(f);
    }
  }

  public void putInt(int i) {
    memBuffer.putInt(i);
  }

  public void putIntArray(int[] array) {
    putVarInt(array.length);
    memBuffer.putIntArray(array);
  }

  public void putIntNullable(@Nullable Integer i) {
    putBoolean(i != null);
    if (i != null) {
      putInt(i);
    }
  }

  public void putLong(long value) {
    memBuffer.putLong(value);
  }

  public void putLongArray(long[] array) {
    putVarInt(array.length);
    memBuffer.putLongArray(array);
  }

  public void putLongNullable(@Nullable Long l) {
    putBoolean(l != null);
    if (l != null) {
      putLong(l);
    }
  }

  public <K, V> void putMap(
      Map<K, V> map, MemoryEncoderFunction<K> keyWriter, MemoryEncoderFunction<V> valueEncoder) {
    putVarInt(map.size());

    for (Map.Entry<K, V> entry : map.entrySet()) {
      keyWriter.write(this, entry.getKey());
      valueEncoder.write(this, entry.getValue());
    }
  }

  public <T> void putNullable(@Nullable T value, Consumer<T> valueEncoder) {
    putBoolean(value != null);
    if (value != null) {
      valueEncoder.accept(value);
    }
  }

  public void putShort(short value) {
    memBuffer.putShort(value);
  }

  public void putShortArray(short[] array) {
    putVarInt(array.length);
    memBuffer.putShortArray(array);
  }

  public void putShortNullable(@Nullable Short s) {
    putBoolean(s != null);
    if (s != null) {
      putShort(s);
    }
  }

  public void putString(String str) {
    byte[] bytes = str.getBytes(UTF_8);
    putVarInt(bytes.length);
    memBuffer.putByteArray(bytes);
  }

  public void putStringNullable(@Nullable String str) {
    putBoolean(str != null);
    if (str != null) {
      putString(str);
    }
  }

  public void putVarInt(int value) {
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

  public void putVarLong(long value) {
    while ((value & ~0x7FL) != 0) {
      putByte((byte) ((value & 0x7F) | 0x80));
      value >>>= 7;
    }
    putByte((byte) value);
  }

  public void putMemoryEncoder(MemoryEncoder memEncoder) {
    MemoryBuffer source = memEncoder.unwrap();
    long byteLength = source.getLimit() - source.getPosition();

    putVarLong(byteLength);
    memBuffer.copyFrom(source);
  }
}
