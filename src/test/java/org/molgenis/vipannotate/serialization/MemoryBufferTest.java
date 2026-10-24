package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemoryBufferTest {

  @Test
  void setPosition() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      memBuffer.setPosition(2);
      assertEquals(2, memBuffer.getPosition());
    }
  }

  @Test
  void setPositionInvalidNegative() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setPosition(-1));
    }
  }

  @Test
  void setPositionInvalidExceedsCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setPosition(4));
    }
  }

  @Test
  void setLimit() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      memBuffer.setLimit(2);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void setLimitInvalidNegative() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setLimit(-1));
    }
  }

  @Test
  void setLimitInvalidExceedsPosition() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      memBuffer.setPosition(2);
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setLimit(1));
    }
  }

  @Test
  void setLimitInvalidExceedsCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      assertThrows(IllegalArgumentException.class, () -> memBuffer.setLimit(4));
    }
  }

  @Test
  void getCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Short.BYTES * 3)) {
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
  void ensureCapacityHeapBased() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(2)) {
      memBuffer.ensureCapacity(5);
      assertEquals(8, memBuffer.getCapacity()); // 2^3 = 8
    }
  }

  @Test
  void ensureCapacityInvalidHeapBased() {
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[2])) {
      assertThrows(UnsupportedOperationException.class, () -> memBuffer.ensureCapacity(5));
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
  void putBooleanGetBoolean() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      memBuffer.putBoolean(true);
      memBuffer.putBoolean(false);
      memBuffer.putBoolean(true);
      memBuffer.flip();

      assertAll(
          () -> {
            assertTrue(memBuffer.getBoolean());
            assertFalse(memBuffer.getBoolean());
            assertTrue(memBuffer.getBoolean());
            assertEquals(3, memBuffer.getPosition());
          },
          () -> assertTrue(memBuffer.getBooleanAtPosition(0)),
          () -> assertFalse(memBuffer.getBooleanAtPosition(1)),
          () -> assertTrue(memBuffer.getBooleanAtPosition(2)));
    }
  }

  @Test
  void putBooleanIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(1)) {
      memBuffer.putBoolean(true);
      memBuffer.putBoolean(false);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void setBooleanAtIndexGetBooleanAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      memBuffer.putBooleanAtIndex(0, true);
      memBuffer.putBooleanAtIndex(1, false);
      memBuffer.putBooleanAtIndex(2, true);
      assertAll(
          () -> assertTrue(memBuffer.getBooleanAtIndex(0)),
          () -> assertFalse(memBuffer.getBooleanAtIndex(1)),
          () -> assertTrue(memBuffer.getBooleanAtIndex(2)),
          () -> assertEquals((byte) 0, memBuffer.getPosition()));
    }
  }

  @Test
  void setBooleanAtIndexIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(1)) {
      memBuffer.putBooleanAtIndex(0, true);
      memBuffer.putBooleanAtIndex(1, false);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void putByteGetByte() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
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
          () -> assertEquals((byte) 0, memBuffer.getByteAtPosition(0)),
          () -> assertEquals((byte) 1, memBuffer.getByteAtPosition(1)),
          () -> assertEquals((byte) 2, memBuffer.getByteAtPosition(2)));
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
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
      memBuffer.putByteAtIndex(0, (byte) 2);
      memBuffer.putByteAtIndex(1, (byte) 0);
      memBuffer.putByteAtIndex(2, (byte) 1);
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
      memBuffer.putByteAtIndex(0, (byte) 0);
      memBuffer.putByteAtIndex(1, (byte) 1);
      assertEquals(2, memBuffer.getLimit());
    }
  }

  @Test
  void putByteArrayGetByteArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      byte[] byteArray = {0, 1, 2, 3};
      memBuffer.putByteArray(byteArray);
      memBuffer.flip();
      assertArrayEquals(byteArray, memBuffer.getByteArray(byteArray.length));
    }
  }

  @Test
  void putByteArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(2)) {
      memBuffer.putByteArray(new byte[] {0, 1, 2, 3});
      assertEquals(4, memBuffer.getLimit());
    }
  }

  @Test
  void putByteCount() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
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
  void putDoubleGetDouble() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Double.BYTES * 3)) {
      memBuffer.putDouble(0.12);
      memBuffer.putDouble(1.23);
      memBuffer.putDouble(2.34);
      memBuffer.flip();
      assertEquals(0.12, memBuffer.getDouble(), 1e-10);
      assertEquals(1.23, memBuffer.getDouble(), 1e-10);
      assertEquals(2.34, memBuffer.getDouble(), 1e-10);
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {2, 6, 8})
  void putDoubleIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putDouble(0.12);
      memBuffer.putDouble(1.23);
      memBuffer.flip();

      assertEquals(16, memBuffer.getLimit());
    }
  }

  @Test
  void setDoubleAtIndexGetDoubleAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Double.BYTES * 3)) {
      memBuffer.putDoubleAtIndex(0, 2.34);
      memBuffer.putDoubleAtIndex(1, 0.12);
      memBuffer.putDoubleAtIndex(2, 1.23);
      assertAll(
          () -> assertEquals(2.34, memBuffer.getDoubleAtIndex(0), 1e-10),
          () -> assertEquals(0.12, memBuffer.getDoubleAtIndex(1), 1e-10),
          () -> assertEquals(1.23, memBuffer.getDoubleAtIndex(2), 1e-10));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {8, 16})
  void setDoubleAtIndexIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putDoubleAtIndex(0, 0.12);
      memBuffer.putDoubleAtIndex(1, 1.23);
      assertEquals(16, memBuffer.getLimit());
    }
  }

  @Test
  void putDoubleArrayGetDoubleArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(40)) {
      double[] doubleArray = {0.12, 1.23, 2.34, 3.45};
      memBuffer.putDoubleArray(doubleArray);
      memBuffer.flip();
      assertArrayEquals(doubleArray, memBuffer.getDoubleArray(doubleArray.length));
    }
  }

  @Test
  void putDoubleArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      memBuffer.putDoubleArray(new double[] {0.12, 1.23, 2.34, 3.45});
      assertEquals(32, memBuffer.getLimit());
    }
  }

  @Test
  void putFloatGetFloat() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Float.BYTES * 3)) {
      memBuffer.putFloat(0.12f);
      memBuffer.putFloat(1.23f);
      memBuffer.putFloat(2.34f);
      memBuffer.flip();
      assertEquals(0.12f, memBuffer.getFloat(), 1e-10);
      assertEquals(1.23f, memBuffer.getFloat(), 1e-10);
      assertEquals(2.34f, memBuffer.getFloat(), 1e-10);
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 4, 6})
  void putFloatIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putFloat(0.12f);
      memBuffer.putFloat(1.23f);
      memBuffer.flip();

      assertEquals(8, memBuffer.getLimit());
    }
  }

  @Test
  void setFloatAtIndexGetFloatAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Float.BYTES * 3)) {
      memBuffer.putFloatAtIndex(0, 2.34f);
      memBuffer.putFloatAtIndex(1, 0.12f);
      memBuffer.putFloatAtIndex(2, 1.23f);
      assertAll(
          () -> assertEquals(2.34f, memBuffer.getFloatAtIndex(0), 1e-10),
          () -> assertEquals(0.12f, memBuffer.getFloatAtIndex(1), 1e-10),
          () -> assertEquals(1.23f, memBuffer.getFloatAtIndex(2), 1e-10));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 6})
  void setFloatAtIndexIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putFloatAtIndex(0, 0.12f);
      memBuffer.putFloatAtIndex(1, 1.23f);
      assertEquals(8, memBuffer.getLimit());
    }
  }

  @Test
  void putFloatArrayGetFloatArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      float[] floatArray = {0.12f, 1.23f, 2.34f, 3.45f};
      memBuffer.putFloatArray(floatArray);
      memBuffer.flip();
      assertArrayEquals(floatArray, memBuffer.getFloatArray(floatArray.length));
    }
  }

  @Test
  void putFloatArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(10)) {
      memBuffer.putFloatArray(new float[] {0.12f, 1.23f, 2.34f, 3.45f});
      assertEquals(16, memBuffer.getLimit());
    }
  }

  @Test
  void putShortGetShort() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Short.BYTES * 3)) {
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
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Short.BYTES * 3)) {
      memBuffer.putShortAtIndex(0, (short) 2);
      memBuffer.putShortAtIndex(1, (short) 0);
      memBuffer.putShortAtIndex(2, (short) 1);
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
      memBuffer.putShortAtIndex(0, (short) 0);
      memBuffer.putShortAtIndex(1, (short) 1);
      assertEquals(4, memBuffer.getLimit());
    }
  }

  @Test
  void putShortArrayGetShortArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      short[] shortArray = {0, 1, 2, 3};
      memBuffer.putShortArray(shortArray);
      memBuffer.flip();
      assertArrayEquals(shortArray, memBuffer.getShortArray(shortArray.length));
    }
  }

  @Test
  void putShortArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(5)) {
      memBuffer.putShortArray(new short[] {0, 1, 2, 3});
      assertEquals(8, memBuffer.getLimit());
    }
  }

  @Test
  void putIntGetInt() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Integer.BYTES * 3)) {
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
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Integer.BYTES * 3)) {
      memBuffer.putIntAtIndex(0, 2);
      memBuffer.putIntAtIndex(1, 0);
      memBuffer.putIntAtIndex(2, 1);
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
      memBuffer.putIntAtIndex(0, 0);
      memBuffer.putIntAtIndex(1, 1);
      assertEquals(8, memBuffer.getLimit());
    }
  }

  @Test
  void putIntArrayGetIntArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      int[] intArray = {0, 1, 2, 3};
      memBuffer.putIntArray(intArray);
      memBuffer.flip();
      assertArrayEquals(intArray, memBuffer.getIntArray(intArray.length));
    }
  }

  @Test
  void putIntArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(10)) {
      memBuffer.putIntArray(new int[] {0, 1, 2, 3});
      assertEquals(16, memBuffer.getLimit());
    }
  }

  @Test
  void putLongGetLong() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Long.BYTES * 3)) {
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
  void setLongAtIndexGetLongAtIndex() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(Long.BYTES * 3)) {
      memBuffer.putLongAtIndex(0, 2L);
      memBuffer.putLongAtIndex(1, 0L);
      memBuffer.putLongAtIndex(2, 1L);
      assertAll(
          () -> assertEquals(2L, memBuffer.getLongAtIndex(0)),
          () -> assertEquals(0L, memBuffer.getLongAtIndex(1)),
          () -> assertEquals(1L, memBuffer.getLongAtIndex(2)));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {8, 12})
  void setLongAtIndexIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      memBuffer.putLongAtIndex(0, 0L);
      memBuffer.putLongAtIndex(1, 1L);
      assertEquals(16, memBuffer.getLimit());
    }
  }

  @Test
  void putLongArrayGetLongArray() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(40)) {
      long[] longArray = {0L, 1L, 2L, 3L};
      memBuffer.putLongArray(longArray);
      memBuffer.flip();
      assertArrayEquals(longArray, memBuffer.getLongArray(longArray.length));
    }
  }

  @Test
  void putLongArrayIncreaseCapacity() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(20)) {
      memBuffer.putLongArray(new long[] {0L, 1L, 2L, 3L});
      assertEquals(32, memBuffer.getLimit());
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
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(100)) {
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
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
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
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(3)) {
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
        MemoryBuffer dstBuffer = MemoryBuffer.allocate(6)) {
      dstBuffer.putByte((byte) -1);

      for (int i = 0; i < 3; i++) {
        srcBuffer.putByte((byte) i);
      }
      srcBuffer.flip();

      dstBuffer.copyFrom(srcBuffer);
      dstBuffer.flip();

      assertAll(
          () -> assertEquals(-1, dstBuffer.getByte()),
          () -> assertEquals(0, dstBuffer.getByte()),
          () -> assertEquals(1, dstBuffer.getByte()),
          () -> assertEquals(2, dstBuffer.getByte()),
          () -> assertEquals(4, dstBuffer.getPosition()),
          () -> assertEquals(4, dstBuffer.getLimit()));
    }
  }

  @Test
  void copyFromEmpty() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.allocate(0);
        MemoryBuffer dstBuffer = MemoryBuffer.allocate(6)) {
      dstBuffer.putByte((byte) -1);

      srcBuffer.flip();

      dstBuffer.copyFrom(srcBuffer);
      dstBuffer.flip();

      assertAll(
          () -> assertEquals(-1, dstBuffer.getByte()),
          () -> assertEquals(1, dstBuffer.getPosition()),
          () -> assertEquals(1, dstBuffer.getLimit()));
    }
  }

  @Test
  void getByteBuffer() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3, 4})) {
      srcBuffer.setPosition(1);
      srcBuffer.setLimit(4);

      ByteBuffer byteBuffer = srcBuffer.getByteBuffer();
      assertEquals(1, byteBuffer.get());
      assertEquals(2, byteBuffer.get());
      assertEquals(3, byteBuffer.get());
      assertThrows(BufferUnderflowException.class, byteBuffer::get);
    }
  }

  @Test
  void getMemoryBuffer() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3, 4})) {
      srcBuffer.setPosition(1);

      MemoryBuffer memBuffer = srcBuffer.getMemoryBuffer();
      assertAll(
          () -> assertEquals(0, memBuffer.getPosition()),
          () -> assertEquals(1, memBuffer.getByte()),
          () -> assertEquals(2, memBuffer.getByte()),
          () -> assertEquals(3, memBuffer.getByte()),
          () -> assertEquals(4, memBuffer.getByte()),
          () -> assertEquals(4, memBuffer.getLimit()));
    }
  }

  @Test
  void getMemoryBufferLength() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3, 4})) {
      srcBuffer.setPosition(1);

      MemoryBuffer memBuffer = srcBuffer.getMemoryBuffer(3);
      assertAll(
          () -> assertEquals(0, memBuffer.getPosition()),
          () -> assertEquals(1, memBuffer.getByte()),
          () -> assertEquals(2, memBuffer.getByte()),
          () -> assertEquals(3, memBuffer.getLimit()));
    }
  }

  @Test
  void getMemoryBufferLengthNegativeLength() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3, 4})) {
      assertThrows(IllegalArgumentException.class, () -> srcBuffer.getMemoryBuffer(-1L));
    }
  }

  @Test
  void getMemoryBufferLengthIndexOutOfBounds() {
    try (MemoryBuffer srcBuffer = MemoryBuffer.wrap(new byte[] {0, 1, 2, 3, 4})) {
      assertThrows(IndexOutOfBoundsException.class, () -> srcBuffer.getMemoryBuffer(6L));
    }
  }
}
