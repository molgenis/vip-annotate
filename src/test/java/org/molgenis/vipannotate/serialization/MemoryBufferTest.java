package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemoryBufferTest {

  @Test
  void putByteGetByte() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[3])) {
      memoryBuffer.putByte((byte) 0);
      memoryBuffer.putByte((byte) 1);
      memoryBuffer.putByte((byte) 2);
      memoryBuffer.rewind();
      assertEquals((byte) 0, memoryBuffer.getByte());
      assertEquals((byte) 1, memoryBuffer.getByte());
      assertEquals((byte) 2, memoryBuffer.getByte());
    }
  }

  @Test
  void setByteAtIndexGetByteAtIndex() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[3])) {
      memoryBuffer.setByteAtIndex(0, (byte) 2);
      memoryBuffer.setByteAtIndex(1, (byte) 0);
      memoryBuffer.setByteAtIndex(2, (byte) 1);
      assertAll(
          () -> assertEquals((byte) 2, memoryBuffer.getByteAtIndex(0)),
          () -> assertEquals((byte) 0, memoryBuffer.getByteAtIndex(1)),
          () -> assertEquals((byte) 1, memoryBuffer.getByteAtIndex(2)));
    }
  }

  @Test
  void putByteArrayGetByteArray() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.allocate(20)) {
      byte[] byteArray = {0, 1, 2, 3};
      memoryBuffer.putByteArray(byteArray);
      memoryBuffer.rewind();
      assertArrayEquals(byteArray, memoryBuffer.getByteArray());
    }
  }

  @Test
  void putShortGetShort() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new short[3])) {
      memoryBuffer.putShort((short) 0);
      memoryBuffer.putShort((short) 1);
      memoryBuffer.putShort((short) 2);
      memoryBuffer.rewind();
      assertEquals((short) 0, memoryBuffer.getShort());
      assertEquals((short) 1, memoryBuffer.getShort());
      assertEquals((short) 2, memoryBuffer.getShort());
    }
  }

  @Test
  void setShortAtIndexGetShortAtIndex() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new short[3])) {
      memoryBuffer.setShortAtIndex(0, (short) 2);
      memoryBuffer.setShortAtIndex(1, (short) 0);
      memoryBuffer.setShortAtIndex(2, (short) 1);
      assertAll(
          () -> assertEquals((short) 2, memoryBuffer.getShortAtIndex(0)),
          () -> assertEquals((short) 0, memoryBuffer.getShortAtIndex(1)),
          () -> assertEquals((short) 1, memoryBuffer.getShortAtIndex(2)));
    }
  }

  @Test
  void putIntGetInt() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new int[3])) {
      memoryBuffer.putInt(0);
      memoryBuffer.putInt(1);
      memoryBuffer.putInt(2);
      memoryBuffer.rewind();
      assertEquals(0, memoryBuffer.getInt());
      assertEquals(1, memoryBuffer.getInt());
      assertEquals(2, memoryBuffer.getInt());
    }
  }

  @Test
  void setIntAtIndexGetIntAtIndex() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new int[3])) {
      memoryBuffer.setIntAtIndex(0, 2);
      memoryBuffer.setIntAtIndex(1, 0);
      memoryBuffer.setIntAtIndex(2, 1);
      assertAll(
          () -> assertEquals(2, memoryBuffer.getIntAtIndex(0)),
          () -> assertEquals(0, memoryBuffer.getIntAtIndex(1)),
          () -> assertEquals(1, memoryBuffer.getIntAtIndex(2)));
    }
  }

  @Test
  void putIntArrayGetIntArray() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.allocate(20)) {
      int[] intArray = {0, 1, 2, 3};
      memoryBuffer.putIntArray(intArray);
      memoryBuffer.rewind();
      assertArrayEquals(intArray, memoryBuffer.getIntArray());
    }
  }

  private static Stream<Arguments> varUnsignedIntProvider() {
    return Stream.of(
        Arguments.of(0b0, 1),
        Arguments.of(0b1111111, 1),
        Arguments.of(0b11111111, 2),
        Arguments.of(0b111111_11111111, 2),
        Arguments.of(0b1111111_11111111, 3),
        Arguments.of(0b11111_11111111_11111111, 3),
        Arguments.of(0b111111_11111111_11111111, 4),
        Arguments.of(Integer.MAX_VALUE, 5));
  }

  @ParameterizedTest
  @MethodSource("varUnsignedIntProvider")
  void putVarUnsignedIntUnchecked(int unsignedInt, int nrBytesWritten) {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[nrBytesWritten])) {
      memoryBuffer.putVarUnsignedIntUnchecked(unsignedInt);
      memoryBuffer.rewind();
      assertEquals(unsignedInt, memoryBuffer.getVarUnsignedInt());
    }
  }

  @Test
  void putVarUnsignedIntUncheckedMultiple() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100])) {
      memoryBuffer.putVarUnsignedIntUnchecked(0b1111111_11111111);
      memoryBuffer.putVarUnsignedIntUnchecked(0b1111111_11111111);
      memoryBuffer.rewind();
      assertEquals(0b1111111_11111111, memoryBuffer.getVarUnsignedInt());
      assertEquals(0b1111111_11111111, memoryBuffer.getVarUnsignedInt());
    }
  }

  @Test
  void putMixedGetMixedNative() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.allocate(100)) {
      memoryBuffer.putByte(Byte.MAX_VALUE);
      memoryBuffer.putShort(Short.MAX_VALUE);
      memoryBuffer.putInt(Integer.MAX_VALUE);
      memoryBuffer.rewind();
      assertEquals(Byte.MAX_VALUE, memoryBuffer.getByte());
      assertEquals(Short.MAX_VALUE, memoryBuffer.getShort());
      assertEquals(Integer.MAX_VALUE, memoryBuffer.getInt());
    }
  }

  @Test
  void putMixedGetMixedHeapBased() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100])) {
      memoryBuffer.putByte(Byte.MAX_VALUE);
      memoryBuffer.putShort(Short.MAX_VALUE);
      memoryBuffer.putInt(Integer.MAX_VALUE);
      memoryBuffer.rewind();
      assertEquals(Byte.MAX_VALUE, memoryBuffer.getByte());
      assertEquals(Short.MAX_VALUE, memoryBuffer.getShort());
      assertEquals(Integer.MAX_VALUE, memoryBuffer.getInt());
    }
  }
}
