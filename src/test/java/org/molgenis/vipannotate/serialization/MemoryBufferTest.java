package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UncheckedIOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemoryBufferTest {

  @Test
  void setPosition() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.setPosition(2);
      assertEquals(2, memBuffer.getPosition());
    }
  }

  @Test
  void setPositionInvalidNegative() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setPosition(-1));
    }
  }

  @Test
  void setPositionInvalidExceedsCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setPosition(4));
    }
  }

  @Test
  void setLimit() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.setLimit(2);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void setLimitInvalidNegative() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setLimit(-1));
    }
  }

  @Test
  void setLimitInvalidExceedsPosition() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.setPosition(2);
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setLimit(1));
    }
  }

  @Test
  void setLimitInvalidExceedsCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setLimit(4));
    }
  }

  @Test
  void getCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new short[3])) {
      assertEquals(Short.BYTES * 3, memBuffer.getCapacity());
    }
  }

  @Test
  void ensureCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(2)) {
      memBuffer.ensureCapacity(5);
      assertEquals(8, memBuffer.getCapacity()); // 2^3 = 8
    }
  }

  @Test
  void ensureCapacityAligned() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(2, 64)) {
      memBuffer.ensureCapacity(5);
      assertAll(
          () -> assertEquals(64, memBuffer.getCapacity()),
          () -> assertEquals(2, memBuffer.getLimit())); // limit should be untouched
    }
  }

  @Test
  void ensureCapacityAlreadyFits() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(2)) {
      memBuffer.ensureCapacity(1);
      assertEquals(2, memBuffer.getCapacity());
    }
  }

  @Test
  void ensureCapacityInvalidHeapBased() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[2])) {
      assertThrows(UncheckedIOException.class, () -> memBuffer.ensureCapacity(5));
    }
  }

  @Test
  void clear() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(3)) {
      memBuffer.setLimit(2);
      memBuffer.setPosition(1);
      memBuffer.clear();
      assertAll(
          () -> assertEquals(0, memBuffer.getPosition()),
          () -> assertEquals(3, memBuffer.getLimit()));
    }
  }

  @Test
  void flip() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(3)) {
      memBuffer.setLimit(2);
      memBuffer.setPosition(1);
      memBuffer.flip();
      assertAll(
          () -> assertEquals(0, memBuffer.getPosition()),
          () -> assertEquals(1, memBuffer.getLimit()));
    }
  }

  @Test
  void rewind() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(3)) {
      memBuffer.setLimit(3);
      memBuffer.setPosition(2);
      memBuffer.rewind();
      assertAll(
          () -> assertEquals(0, memBuffer.getPosition()),
          () -> assertEquals(3, memBuffer.getLimit()));
    }
  }

  @Test
  void putByteGetByte() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.putByte((byte) 0);
      memBuffer.putByte((byte) 1);
      memBuffer.putByte((byte) 2);
      memBuffer.flip();

      assertAll(
          () -> {
            assertEquals((byte) 0, memBuffer.getByte());
            assertEquals((byte) 1, memBuffer.getByte());
            assertEquals((byte) 2, memBuffer.getByte());
            assertEquals(3, memBuffer.getPosition());
          },
          () -> assertEquals((byte) 0, memBuffer.getByte(0)),
          () -> assertEquals((byte) 1, memBuffer.getByte(1)),
          () -> assertEquals((byte) 2, memBuffer.getByte(2)));
    }
  }

  @Test
  void putByteIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(1)) {
      memBuffer.putByte((byte) 0);
      memBuffer.putByte((byte) 1);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void setByteAtIndexGetByteAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.setByteAtIndex(0, (byte) 2);
      memBuffer.setByteAtIndex(1, (byte) 0);
      memBuffer.setByteAtIndex(2, (byte) 1);
      assertAll(
          () -> assertEquals((byte) 2, memBuffer.getByteAtIndex(0)),
          () -> assertEquals((byte) 0, memBuffer.getByteAtIndex(1)),
          () -> assertEquals((byte) 1, memBuffer.getByteAtIndex(2)),
          () -> assertEquals((byte) 0, memBuffer.getPosition()));
    }
  }

  @Test
  void setByteAtIndexIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(1)) {
      memBuffer.setByteAtIndex(0, (byte) 0);
      memBuffer.setByteAtIndex(1, (byte) 1);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void putByteArrayGetByteArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      byte[] byteArray = {0, 1, 2, 3};
      memBuffer.putByteArray(byteArray);
      memBuffer.flip();
      assertArrayEquals(byteArray, memBuffer.getByteArray());
    }
  }

  @Test
  void putByteArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(3)) {
      memBuffer.putByteArray(new byte[] {0, 1, 2, 3});
      assertEquals(5, memBuffer.getLimit());
    }
  }

  @Test
  void putByteCount() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.putByte((byte) 1, 3);
      assertAll(
          () -> assertEquals((byte) 1, memBuffer.getByteAtIndex(0)),
          () -> assertEquals((byte) 1, memBuffer.getByteAtIndex(1)),
          () -> assertEquals((byte) 1, memBuffer.getByteAtIndex(2)),
          () -> assertEquals((byte) 3, memBuffer.getPosition()));
    }
  }

  @Test
  void putByteCountIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(2)) {
      memBuffer.putByte((byte) 1, 3);
      assertAll(() -> assertEquals((byte) 3, memBuffer.getLimit()));
    }
  }

  @Test
  void putShortGetShort() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new short[3])) {
      memBuffer.putShort((short) 0);
      memBuffer.putShort((short) 1);
      memBuffer.putShort((short) 2);
      memBuffer.flip();
      assertEquals((short) 0, memBuffer.getShort());
      assertEquals((short) 1, memBuffer.getShort());
      assertEquals((short) 2, memBuffer.getShort());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3})
  void putShortIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putShort((short) 0);
      memBuffer.putShort((short) 1);
      memBuffer.flip();

      assertEquals(4, memBuffer.getLimit());
    }
  }

  @Test
  void setShortAtIndexGetShortAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new short[3])) {
      memBuffer.setShortAtIndex(0, (short) 2);
      memBuffer.setShortAtIndex(1, (short) 0);
      memBuffer.setShortAtIndex(2, (short) 1);
      assertAll(
          () -> assertEquals((short) 2, memBuffer.getShortAtIndex(0)),
          () -> assertEquals((short) 0, memBuffer.getShortAtIndex(1)),
          () -> assertEquals((short) 1, memBuffer.getShortAtIndex(2)));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 3})
  void setShortAtIndexIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.setShortAtIndex(0, (short) 0);
      memBuffer.setShortAtIndex(1, (short) 1);
      assertEquals(4, memBuffer.getLimit());
    }
  }

  @Test
  void putIntGetInt() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new int[3])) {
      memBuffer.putInt(0);
      memBuffer.putInt(1);
      memBuffer.putInt(2);
      memBuffer.flip();
      assertEquals(0, memBuffer.getInt());
      assertEquals(1, memBuffer.getInt());
      assertEquals(2, memBuffer.getInt());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 4, 6})
  void putIntIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putInt(0);
      memBuffer.putInt(1);
      memBuffer.flip();

      assertEquals(8, memBuffer.getLimit());
    }
  }

  @Test
  void setIntAtIndexGetIntAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new int[3])) {
      memBuffer.setIntAtIndex(0, 2);
      memBuffer.setIntAtIndex(1, 0);
      memBuffer.setIntAtIndex(2, 1);
      assertAll(
          () -> assertEquals(2, memBuffer.getIntAtIndex(0)),
          () -> assertEquals(0, memBuffer.getIntAtIndex(1)),
          () -> assertEquals(1, memBuffer.getIntAtIndex(2)));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 6})
  void setIntAtIndexIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.setIntAtIndex(0, 0);
      memBuffer.setIntAtIndex(1, 1);
      assertEquals(8, memBuffer.getLimit());
    }
  }

  @Test
  void putIntArrayGetIntArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      int[] intArray = {0, 1, 2, 3};
      memBuffer.putIntArray(intArray);
      memBuffer.flip();
      assertArrayEquals(intArray, memBuffer.getIntArray());
    }
  }

  @Test
  void putIntArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(10)) {
      memBuffer.putIntArray(new int[] {0, 1, 2, 3});
      assertEquals(17, memBuffer.getLimit());
    }
  }

  @Test
  void putVarUnsignedIntGetVarUnsigned() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[15])) {
      memBuffer.putVarUnsignedInt(0);
      memBuffer.putVarUnsignedInt(1);
      memBuffer.putVarUnsignedInt(2);
      memBuffer.flip();
      assertEquals(0, memBuffer.getVarUnsignedInt());
      assertEquals(1, memBuffer.getVarUnsignedInt());
      assertEquals(2, memBuffer.getVarUnsignedInt());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {2, 3})
  void putVarUnsignedIntGetVarUnsignedIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putVarUnsignedInt(256);
      memBuffer.putVarUnsignedInt(257);
      memBuffer.flip();

      assertEquals(4, memBuffer.getLimit());
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
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[nrBytesWritten])) {
      memBuffer.putVarUnsignedIntUnchecked(unsignedInt);
      memBuffer.flip();
      assertEquals(unsignedInt, memBuffer.getVarUnsignedInt());
    }
  }

  @Test
  void putVarUnsignedIntUncheckedMultiple() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[100])) {
      memBuffer.putVarUnsignedIntUnchecked(0b1111111_11111111);
      memBuffer.putVarUnsignedIntUnchecked(0b1111111_11111111);
      memBuffer.flip();
      assertEquals(0b1111111_11111111, memBuffer.getVarUnsignedInt());
      assertEquals(0b1111111_11111111, memBuffer.getVarUnsignedInt());
    }
  }

  @Test
  void putLongGetLong() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new long[3])) {
      memBuffer.putLong(0L);
      memBuffer.putLong(1L);
      memBuffer.putLong(2L);
      memBuffer.flip();
      assertEquals(0L, memBuffer.getLong());
      assertEquals(1L, memBuffer.getLong());
      assertEquals(2L, memBuffer.getLong());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 8, 11})
  void putLongIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putLong(0);
      memBuffer.putLong(1);
      memBuffer.flip();

      assertEquals(16, memBuffer.getLimit());
    }
  }

  @Test
  void putMixedGetMixedNative() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(100)) {
      memBuffer.putByte(Byte.MAX_VALUE);
      memBuffer.putShort(Short.MAX_VALUE);
      memBuffer.putInt(Integer.MAX_VALUE);
      memBuffer.flip();
      assertEquals(Byte.MAX_VALUE, memBuffer.getByte());
      assertEquals(Short.MAX_VALUE, memBuffer.getShort());
      assertEquals(Integer.MAX_VALUE, memBuffer.getInt());
    }
  }

  @Test
  void putMixedGetMixedHeapBased() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[100])) {
      memBuffer.putByte(Byte.MAX_VALUE);
      memBuffer.putShort(Short.MAX_VALUE);
      memBuffer.putInt(Integer.MAX_VALUE);
      memBuffer.flip();
      assertEquals(Byte.MAX_VALUE, memBuffer.getByte());
      assertEquals(Short.MAX_VALUE, memBuffer.getShort());
      assertEquals(Integer.MAX_VALUE, memBuffer.getInt());
    }
  }

  @Test
  void getMemSegment() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.putByte((byte) 0);
      memBuffer.putByte((byte) 1);
      memBuffer.putByte((byte) 2);
      memBuffer.flip();

      MemorySegment memSegment = memBuffer.getMemSegment();
      assertAll(
          () -> assertEquals(0, memSegment.get(ValueLayout.JAVA_BYTE, 0)),
          () -> assertEquals(1, memSegment.get(ValueLayout.JAVA_BYTE, 1)),
          () -> assertEquals(2, memSegment.get(ValueLayout.JAVA_BYTE, 2)),
          () -> assertEquals(3, memBuffer.getPosition()));
    }
  }

  @Test
  void getMemSegmentAfterGetByte() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      memBuffer.putByte((byte) 0);
      memBuffer.putByte((byte) 1);
      memBuffer.putByte((byte) 2);
      memBuffer.flip();

      memBuffer.getByte();
      MemorySegment memSegment = memBuffer.getMemSegment();
      assertAll(
          () -> assertEquals(1, memSegment.get(ValueLayout.JAVA_BYTE, 0)),
          () -> assertEquals(2, memSegment.get(ValueLayout.JAVA_BYTE, 1)),
          () -> assertEquals(3, memBuffer.getPosition()));
    }
  }

  @Test
  void getMemSegmentPreserveAlignment() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(4, 4)) {
      for (int i = 0; i < 8; i++) {
        memBuffer.putByte((byte) i);
      }
      memBuffer.flip();

      for (int i = 0; i < 4; i++) {
        memBuffer.getByte();
      }
      MemorySegment memSegment = memBuffer.getMemSegment();
      assertEquals(0, memSegment.address() % 4);
    }
  }

  @Test
  void getMemSegmentPreserveAlignmentMaybe() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(8, 4)) {
      for (int i = 0; i < 8; i++) {
        memBuffer.putByte((byte) i);
      }
      memBuffer.flip();

      for (int i = 0; i < 5; i++) {
        memBuffer.getByte();
      }
      MemorySegment memSegment = memBuffer.getMemSegment();
      assertAll(
          () -> assertEquals(5, memSegment.get(ValueLayout.JAVA_BYTE, 0)),
          () -> assertEquals(6, memSegment.get(ValueLayout.JAVA_BYTE, 1)),
          () -> assertEquals(7, memSegment.get(ValueLayout.JAVA_BYTE, 2)),
          () -> assertEquals(8, memBuffer.getPosition()));
    }
  }

  @Test
  void copyFrom() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.allocate(5);
        MemoryBuffer dstBuffer = MemoryBuffer.allocate(3)) {
      for (int i = 0; i < 3; i++) {
        srcBuffer.putByte((byte) i);
      }
      srcBuffer.flip();

      dstBuffer.copyFrom(srcBuffer);
      assertAll(
          () -> assertEquals(0, dstBuffer.getByte()),
          () -> assertEquals(1, dstBuffer.getByte()),
          () -> assertEquals(2, dstBuffer.getByte()),
          () -> assertEquals(3, dstBuffer.getPosition()));
    }
  }
}
