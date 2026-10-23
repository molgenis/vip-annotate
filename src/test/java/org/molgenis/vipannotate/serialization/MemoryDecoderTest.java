package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemoryDecoderTest {
  @Mock private MemoryBuffer memBuffer;
  private MemoryDecoder memDecoder;

  @BeforeEach
  void setUp() {
    memDecoder = MemoryDecoder.wrap(memBuffer);
  }

  @Test
  void getArray() {
    when(memBuffer.getByte()).thenReturn((byte) 3);
    when(memBuffer.getInt())
        .thenReturn(Integer.MIN_VALUE)
        .thenReturn(0)
        .thenReturn(Integer.MAX_VALUE);
    assertArrayEquals(
        new Integer[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE},
        memDecoder.getArray(Integer[]::new, MemoryDecoder::getInt));
  }

  @Test
  void getByte() {
    when(memBuffer.getByte()).thenReturn((byte) 123);
    assertEquals((byte) 123, memDecoder.getByte());
  }

  @Test
  void getByteAtIndex() {
    when(memBuffer.getByteAtIndex(1L)).thenReturn((byte) 123);
    assertEquals((byte) 123, memDecoder.getByteAtIndex(1L));
  }

  @Test
  void getUnsignedByte() {
    when(memBuffer.getByte()).thenReturn((byte) -56);
    assertEquals(200, memDecoder.getUnsignedByte());
  }

  @Test
  void getUnsignedByteAtIndex() {
    when(memBuffer.getByteAtIndex(1L)).thenReturn((byte) -56);
    assertEquals(200, memDecoder.getUnsignedByteAtIndex(1L));
  }

  @Test
  void getByteNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getByte()).thenReturn((byte) 1);
    assertEquals(Byte.valueOf((byte) 1), memDecoder.getByteNullable());
  }

  @Test
  void getByteNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getByteNullable());
  }

  @Test
  void getByteArray() {
    byte[] values = {0, 1, 2};
    when(memBuffer.getByte()).thenReturn((byte) values.length);
    when(memBuffer.getByteArray(values.length)).thenReturn(values);
    assertArrayEquals(values, memDecoder.getByteArray());
  }

  @Test
  void getDouble() {
    when(memBuffer.getDouble()).thenReturn(1.23);
    assertEquals(1.23, memDecoder.getDouble());
  }

  @Test
  void getDoubleAtIndex() {
    when(memBuffer.getDoubleAtIndex(1L)).thenReturn(1.23);
    assertEquals(1.23, memDecoder.getDoubleAtIndex(1L));
  }

  @Test
  void getDoubleNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getDouble()).thenReturn(1.23);
    assertEquals(Double.valueOf(1.23), memDecoder.getDoubleNullable());
  }

  @Test
  void getDoubleNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getDoubleNullable());
  }

  @Test
  void getDoubleArray() {
    double[] values = {0.12, 1.23, 2.34};
    when(memBuffer.getByte()).thenReturn((byte) values.length);
    when(memBuffer.getDoubleArray(values.length)).thenReturn(values);
    assertArrayEquals(values, memDecoder.getDoubleArray());
  }

  private enum MyEnum {
    ONE,
    TWO,
    THREE
  }

  @Test
  void getEnum() {
    when(memBuffer.getByte()).thenReturn((byte) 1);
    assertEquals(MyEnum.TWO, memDecoder.getEnum(MyEnum.class));
  }

  @Test
  void getEnumSet() {
    when(memBuffer.getByte()).thenReturn((byte) 2).thenReturn((byte) 0).thenReturn((byte) 1);
    assertEquals(EnumSet.of(MyEnum.ONE, MyEnum.TWO), memDecoder.getEnumSet(MyEnum.class));
  }

  @Test
  void getFloat() {
    when(memBuffer.getFloat()).thenReturn(1.23f);
    assertEquals(1.23f, memDecoder.getFloat());
  }

  @Test
  void getFloatAtIndex() {
    when(memBuffer.getFloatAtIndex(1L)).thenReturn(1.23f);
    assertEquals(1.23f, memDecoder.getFloatAtIndex(1L));
  }

  @Test
  void getFloatNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getFloat()).thenReturn(1.23f);
    assertEquals(Float.valueOf(1.23f), memDecoder.getFloatNullable());
  }

  @Test
  void getFloatNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getFloatNullable());
  }

  @Test
  void getFloatArray() {
    float[] values = {0.12f, 1.23f, 2.34f};
    when(memBuffer.getByte()).thenReturn((byte) values.length);
    when(memBuffer.getFloatArray(values.length)).thenReturn(values);
    assertArrayEquals(values, memDecoder.getFloatArray());
  }

  @Test
  void getInt() {
    when(memBuffer.getInt()).thenReturn(1);
    assertEquals(1, memDecoder.getInt());
  }

  @Test
  void getIntAtIndex() {
    when(memBuffer.getIntAtIndex(1L)).thenReturn(2);
    assertEquals(2, memDecoder.getIntAtIndex(1L));
  }

  @Test
  void getIntNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getInt()).thenReturn(1);
    assertEquals(Integer.valueOf(1), memDecoder.getIntNullable());
  }

  @Test
  void getIntNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getIntNullable());
  }

  @Test
  void getUnsignedInt() {
    when(memBuffer.getInt()).thenReturn(-1);
    assertEquals(4294967295L, memDecoder.getUnsignedInt());
  }

  @Test
  void getUnsignedIntAtIndex() {
    when(memBuffer.getIntAtIndex(1L)).thenReturn(-2);
    assertEquals(4294967294L, memDecoder.getUnsignedIntAtIndex(1L));
  }

  @Test
  void getIntArray() {
    int[] values = {0, 1, 2};
    when(memBuffer.getByte()).thenReturn((byte) values.length);
    when(memBuffer.getIntArray(values.length)).thenReturn(values);
    assertArrayEquals(values, memDecoder.getIntArray());
  }

  @Test
  void getLong() {
    when(memBuffer.getLong()).thenReturn(1L);
    assertEquals(1L, memDecoder.getLong());
  }

  @Test
  void getLongAtIndex() {
    when(memBuffer.getLongAtIndex(1L)).thenReturn(2L);
    assertEquals(2L, memDecoder.getLongAtIndex(1L));
  }

  @Test
  void getLongNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getLong()).thenReturn(1L);
    assertEquals(Long.valueOf(1L), memDecoder.getLongNullable());
  }

  @Test
  void getLongNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getLongNullable());
  }

  @Test
  void getLongArray() {
    long[] values = {0L, 1L, 2L};
    when(memBuffer.getByte()).thenReturn((byte) values.length);
    when(memBuffer.getLongArray(values.length)).thenReturn(values);
    assertArrayEquals(values, memDecoder.getLongArray());
  }

  @Test
  void getMap() {
    when(memBuffer.getByte())
        .thenReturn((byte) 3)
        .thenReturn((byte) 1)
        .thenReturn((byte) 2)
        .thenReturn((byte) 3);
    when(memBuffer.getInt()).thenReturn(4).thenReturn(5).thenReturn(6);
    when(memBuffer.getByteArray(1)).thenReturn(new byte[] {'a'});
    when(memBuffer.getByteArray(2)).thenReturn(new byte[] {'a', 'b'});
    when(memBuffer.getByteArray(3)).thenReturn(new byte[] {'a', 'b', 'c'});

    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("a", 4);
    map.put("ab", 5);
    map.put("abc", 6);
    assertEquals(map, memDecoder.getMap(MemoryDecoder::getString, MemoryDecoder::getInt));
  }

  @Test
  void getMapInto() {
    when(memBuffer.getByte())
        .thenReturn((byte) 3)
        .thenReturn((byte) 1)
        .thenReturn((byte) 2)
        .thenReturn((byte) 3);
    when(memBuffer.getInt()).thenReturn(4).thenReturn(5).thenReturn(6);
    when(memBuffer.getByteArray(1)).thenReturn(new byte[] {'a'});
    when(memBuffer.getByteArray(2)).thenReturn(new byte[] {'a', 'b'});
    when(memBuffer.getByteArray(3)).thenReturn(new byte[] {'a', 'b', 'c'});

    Map<String, Integer> map = new LinkedHashMap<>();
    memDecoder.getMapInto(map, MemoryDecoder::getString, MemoryDecoder::getInt);

    Map<String, Integer> expectedMap = new LinkedHashMap<>();
    expectedMap.put("a", 4);
    expectedMap.put("ab", 5);
    expectedMap.put("abc", 6);
    assertEquals(expectedMap, map);
  }

  @Test
  void getNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getInt()).thenReturn(2);
    assertEquals(Integer.valueOf(2), memDecoder.getNullable(memDecoder::getIntNullable));
  }

  @Test
  void getNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getNullable(memDecoder::getIntNullable));
  }

  @Test
  void getShort() {
    when(memBuffer.getShort()).thenReturn((short) 1);
    assertEquals((short) 1, memDecoder.getShort());
  }

  @Test
  void getShortAtIndex() {
    when(memBuffer.getShortAtIndex(1L)).thenReturn((short) 2);
    assertEquals((short) 2, memDecoder.getShortAtIndex(1L));
  }

  @Test
  void getShortNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getShort()).thenReturn((short) 1);
    assertEquals(Short.valueOf((short) 1), memDecoder.getShortNullable());
  }

  @Test
  void getShortNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getShortNullable());
  }

  @Test
  void getUnsignedShort() {
    when(memBuffer.getShort()).thenReturn((short) -56);
    assertEquals(65480, memDecoder.getUnsignedShort());
  }

  @Test
  void getUnsignedShortAtIndex() {
    when(memBuffer.getShortAtIndex(1L)).thenReturn((short) -56);
    assertEquals(65480, memDecoder.getUnsignedShortAtIndex(1L));
  }

  @Test
  void getShortArray() {
    short[] values = {0, 1, 2};
    when(memBuffer.getByte()).thenReturn((byte) values.length);
    when(memBuffer.getShortArray(values.length)).thenReturn(values);
    assertArrayEquals(values, memDecoder.getShortArray());
  }

  @Test
  void getString() {
    when(memBuffer.getByte()).thenReturn((byte) 3);
    when(memBuffer.getByteArray(3)).thenReturn(new byte[] {'a', 'b', 'c'});
    assertEquals("abc", memDecoder.getString());
  }

  @Test
  void getStringNullableNonNull() {
    when(memBuffer.getBoolean()).thenReturn(true);
    when(memBuffer.getByte()).thenReturn((byte) 3);
    when(memBuffer.getByteArray(3)).thenReturn(new byte[] {'a', 'b', 'c'});
    assertEquals("abc", memDecoder.getStringNullable());
  }

  @Test
  void getStringNullableNull() {
    when(memBuffer.getBoolean()).thenReturn(false);
    assertNull(memDecoder.getStringNullable());
  }

  @Test
  void unwrap() {
    assertEquals(memBuffer, memDecoder.unwrap());
  }
}
