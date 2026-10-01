package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutoGrowingMemoryEncoderTest {
  @Mock private MemoryBuffer memBuffer;
  private MemoryEncoder memEncoder;

  @BeforeEach
  void setUp() {
    memEncoder = AutoGrowingMemoryEncoder.wrap(memBuffer);
  }

  @Test
  void putArray() {
    memEncoder.putArray(
        new Integer[] {-1, null, 0, 1},
        (memEncoder, integer) -> memEncoder.putInt(integer != null ? integer : Integer.MAX_VALUE));

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 4);
    inOrder.verify(memBuffer).putInt(-1);
    inOrder.verify(memBuffer).putInt(Integer.MAX_VALUE);
    inOrder.verify(memBuffer).putInt(0);
    inOrder.verify(memBuffer).putInt(1);
  }

  @Test
  void putBoolean() {
    memEncoder.putBoolean(true);
    verify(memBuffer).putBoolean(true);
  }

  @Test
  void putByte() {
    memEncoder.putByte((byte) 0);
    verify(memBuffer).putByte((byte) 0);
  }

  @Test
  void putByteAtIndex() {
    memEncoder.putByteAtIndex(0, (byte) 1);
    verify(memBuffer).putByteAtIndex(0, (byte) 1);
  }

  @Test
  void putByteArray() {
    byte[] array = {Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
    memEncoder.putByteArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putByteArray(array);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putDouble() {
    memEncoder.putDouble(1.23);
    verify(memBuffer).putDouble(1.23);
  }

  @Test
  void putDoubleAtIndex() {
    memEncoder.putDoubleAtIndex(1, 2.34);
    verify(memBuffer).putDoubleAtIndex(1, 2.34);
  }

  @Test
  void putDoubleArray() {
    double[] array = {Double.MIN_VALUE, 0d, Double.MAX_VALUE};
    memEncoder.putDoubleArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putDoubleArray(array);
  }

  private enum MyEnum {
    ONE,
    TWO,
    THREE
  }

  @Test
  void putEnum() {
    memEncoder.putEnum(MyEnum.TWO);
    verify(memBuffer).putByte((byte) 1);
  }

  @Test
  void putEnumSet() {
    memEncoder.putEnumSet(EnumSet.of(MyEnum.ONE, MyEnum.THREE));

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 2);
    inOrder.verify(memBuffer).putByte((byte) 0);
    inOrder.verify(memBuffer).putByte((byte) 2);
  }

  @Test
  void putFloat() {
    memEncoder.putFloat(1.23f);
    verify(memBuffer).putFloat(1.23f);
  }

  @Test
  void putFloatAtIndex() {
    memEncoder.putFloatAtIndex(1, 2.34f);
    verify(memBuffer).putFloatAtIndex(1, 2.34f);
  }

  @Test
  void putFloatArray() {
    float[] array = {Float.MIN_VALUE, 0f, Float.MAX_VALUE};
    memEncoder.putFloatArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putFloatArray(array);
  }

  @Test
  void putInt() {
    memEncoder.putInt(3);
    verify(memBuffer).putInt(3);
  }

  @Test
  void putIntAtIndex() {
    memEncoder.putIntAtIndex(2, -3);
    verify(memBuffer).putIntAtIndex(2, -3);
  }

  @Test
  void putIntArray() {
    int[] array = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
    memEncoder.putIntArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putIntArray(array);
  }

  @Test
  void putLong() {
    memEncoder.putLong(Long.MAX_VALUE);
    verify(memBuffer).putLong(Long.MAX_VALUE);
  }

  @Test
  void putLongAtIndex() {
    memEncoder.putLongAtIndex(1, Long.MIN_VALUE);
    verify(memBuffer).putLongAtIndex(1, Long.MIN_VALUE);
  }

  @Test
  void putLongArray() {
    long[] array = {Long.MIN_VALUE, 0L, Long.MAX_VALUE};
    memEncoder.putLongArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putLongArray(array);
  }

  @Test
  void putMap() {
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("a", 4);
    map.put("bcd", 5);

    memEncoder.putMap(map, MemoryEncoder::putString, MemoryEncoder::putInt);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 2);

    inOrder.verify(memBuffer).putByte((byte) 1);
    inOrder.verify(memBuffer).putByteArray(new byte[] {'a'});
    inOrder.verify(memBuffer).putInt(4);

    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putByteArray(new byte[] {'b', 'c', 'd'});
    inOrder.verify(memBuffer).putInt(5);
  }

  @Test
  void putShort() {
    memEncoder.putShort(Short.MAX_VALUE);
    verify(memBuffer).putShort(Short.MAX_VALUE);
  }

  @Test
  void putShortAtIndex() {
    memEncoder.putShortAtIndex(1, Short.MIN_VALUE);
    verify(memBuffer).putShortAtIndex(1, Short.MIN_VALUE);
  }

  @Test
  void putShortArray() {
    short[] array = {Short.MIN_VALUE, (short) 0, Short.MAX_VALUE};
    memEncoder.putShortArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putShortArray(array);
  }

  @Test
  void putString() {
    memEncoder.putString("abc");

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByte((byte) 3);
    inOrder.verify(memBuffer).putByteArray(new byte[] {'a', 'b', 'c'});
  }

  @Test
  void unwrap() {
    assertEquals(memBuffer, memEncoder.unwrap());
  }
}
