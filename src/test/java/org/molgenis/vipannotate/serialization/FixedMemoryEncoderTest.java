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
class FixedMemoryEncoderTest {
  @Mock private MemoryBuffer memBuffer;
  private MemoryEncoder memEncoder;

  @BeforeEach
  void setUp() {
    memEncoder = FixedMemoryEncoder.wrap(memBuffer);
  }

  @Test
  void putArray() {
    memEncoder.putArray(
        new Integer[] {-1, null, 0, 1},
        (memEncoder, integer) -> memEncoder.putInt(integer != null ? integer : Integer.MAX_VALUE));

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 4);
    inOrder.verify(memBuffer).putIntUnchecked(-1);
    inOrder.verify(memBuffer).putIntUnchecked(Integer.MAX_VALUE);
    inOrder.verify(memBuffer).putIntUnchecked(0);
    inOrder.verify(memBuffer).putIntUnchecked(1);
  }

  @Test
  void putBoolean() {
    memEncoder.putBoolean(true);
    verify(memBuffer).putBooleanUnchecked(true);
  }

  @Test
  void putByte() {
    memEncoder.putByte((byte) 0);
    verify(memBuffer).putByteUnchecked((byte) 0);
  }

  @Test
  void putByteAtIndex() {
    memEncoder.putByteAtIndex(0, (byte) 1);
    verify(memBuffer).putByteAtIndexUnchecked(0, (byte) 1);
  }

  @Test
  void putByteArray() {
    byte[] array = {Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
    memEncoder.putByteArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putByteArrayUnchecked(array);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void putDouble() {
    memEncoder.putDouble(1.23);
    verify(memBuffer).putDoubleUnchecked(1.23);
  }

  @Test
  void putDoubleAtIndex() {
    memEncoder.putDoubleAtIndex(1, 2.34);
    verify(memBuffer).putDoubleAtIndexUnchecked(1, 2.34);
  }

  @Test
  void putDoubleArray() {
    double[] array = {Double.MIN_VALUE, 0d, Double.MAX_VALUE};
    memEncoder.putDoubleArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putDoubleArrayUnchecked(array);
  }

  private enum MyEnum {
    ONE,
    TWO,
    THREE
  }

  @Test
  void putEnum() {
    memEncoder.putEnum(MyEnum.TWO);
    verify(memBuffer).putByteUnchecked((byte) 1);
  }

  @Test
  void putEnumSet() {
    memEncoder.putEnumSet(EnumSet.of(MyEnum.ONE, MyEnum.THREE));

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 2);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 0);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 2);
  }

  @Test
  void putFloat() {
    memEncoder.putFloat(1.23f);
    verify(memBuffer).putFloatUnchecked(1.23f);
  }

  @Test
  void putFloatAtIndex() {
    memEncoder.putFloatAtIndex(1, 2.34f);
    verify(memBuffer).putFloatAtIndexUnchecked(1, 2.34f);
  }

  @Test
  void putFloatArray() {
    float[] array = {Float.MIN_VALUE, 0f, Float.MAX_VALUE};
    memEncoder.putFloatArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putFloatArrayUnchecked(array);
  }

  @Test
  void putInt() {
    memEncoder.putInt(3);
    verify(memBuffer).putIntUnchecked(3);
  }

  @Test
  void putIntAtIndex() {
    memEncoder.putIntAtIndex(2, -3);
    verify(memBuffer).putIntAtIndexUnchecked(2, -3);
  }

  @Test
  void putIntArray() {
    int[] array = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
    memEncoder.putIntArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putIntArrayUnchecked(array);
  }

  @Test
  void putLong() {
    memEncoder.putLong(Long.MAX_VALUE);
    verify(memBuffer).putLongUnchecked(Long.MAX_VALUE);
  }

  @Test
  void putLongAtIndex() {
    memEncoder.putLongAtIndex(1, Long.MIN_VALUE);
    verify(memBuffer).putLongAtIndexUnchecked(1, Long.MIN_VALUE);
  }

  @Test
  void putLongArray() {
    long[] array = {Long.MIN_VALUE, 0L, Long.MAX_VALUE};
    memEncoder.putLongArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putLongArrayUnchecked(array);
  }

  @Test
  void putMap() {
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("a", 4);
    map.put("bcd", 5);

    memEncoder.putMap(map, MemoryEncoder::putString, MemoryEncoder::putInt);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 2);

    inOrder.verify(memBuffer).putByteUnchecked((byte) 1);
    inOrder.verify(memBuffer).putByteArrayUnchecked(new byte[] {'a'});
    inOrder.verify(memBuffer).putIntUnchecked(4);

    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putByteArrayUnchecked(new byte[] {'b', 'c', 'd'});
    inOrder.verify(memBuffer).putIntUnchecked(5);
  }

  @Test
  void putShort() {
    memEncoder.putShort(Short.MAX_VALUE);
    verify(memBuffer).putShortUnchecked(Short.MAX_VALUE);
  }

  @Test
  void putShortAtIndex() {
    memEncoder.putShortAtIndex(1, Short.MIN_VALUE);
    verify(memBuffer).putShortAtIndexUnchecked(1, Short.MIN_VALUE);
  }

  @Test
  void putShortArray() {
    short[] array = {Short.MIN_VALUE, (short) 0, Short.MAX_VALUE};
    memEncoder.putShortArray(array);

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putShortArrayUnchecked(array);
  }

  @Test
  void putString() {
    memEncoder.putString("abc");

    InOrder inOrder = inOrder(memBuffer);
    inOrder.verify(memBuffer).putByteUnchecked((byte) 3);
    inOrder.verify(memBuffer).putByteArrayUnchecked(new byte[] {'a', 'b', 'c'});
  }

  @Test
  void unwrap() {
    assertEquals(memBuffer, memEncoder.unwrap());
  }
}
