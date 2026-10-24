package org.molgenis.vipannotate.serialization;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemoryEncoderDecoderIT {
  @Test
  void putVarIntGetVarInt() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(15)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarInt(0);
      memEncoder.putVarInt(1);
      memEncoder.putVarInt(2);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(0, memDecoder.getVarInt());
      assertEquals(1, memDecoder.getVarInt());
      assertEquals(2, memDecoder.getVarInt());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {2, 3})
  void putVarIntGetVarIntIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarInt(256);
      memEncoder.putVarInt(257);
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
  void putVarIntUnchecked(int unsignedInt, int nrBytesWritten) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(nrBytesWritten)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarInt(unsignedInt);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(unsignedInt, memDecoder.getVarInt());
    }
  }

  @Test
  void putVarIntUncheckedMultiple() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(100)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarInt(0b1111111_11111111);
      memEncoder.putVarInt(0b1111111_11111111);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(0b1111111_11111111, memDecoder.getVarInt());
      assertEquals(0b1111111_11111111, memDecoder.getVarInt());
    }
  }

  @Test
  void putVarLongGetVarLong() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(100)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarLong(0L);
      memEncoder.putVarLong(1L);
      memEncoder.putVarLong(2L);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(0L, memDecoder.getVarLong());
      assertEquals(1L, memDecoder.getVarLong());
      assertEquals(2L, memDecoder.getVarLong());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {2, 3})
  void putVarLongGetVarLongIncreaseCapacity(int capacity) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(capacity)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarLong(256L);
      memEncoder.putVarLong(257L);
      memBuffer.flip();

      assertEquals(4, memBuffer.getLimit());
    }
  }

  private static Stream<Arguments> varUnsignedLongProvider() {
    return Stream.of(
        Arguments.of(0L, 1),
        Arguments.of(0b1111111L, 1),
        Arguments.of(0b11111111L, 2),
        Arguments.of(0b111111_11111111L, 2),
        Arguments.of(0b1111111_11111111L, 3),
        Arguments.of(0b111111_11111111_11111111L, 4),
        Arguments.of(0b1111111_11111111_11111111L, 4),
        Arguments.of((1L << 28) - 1, 4),
        Arguments.of(1L << 28, 5),
        Arguments.of((1L << 35) - 1, 5),
        Arguments.of(1L << 35, 6),
        Arguments.of((1L << 42) - 1, 6),
        Arguments.of(1L << 42, 7),
        Arguments.of((1L << 49) - 1, 7),
        Arguments.of(1L << 49, 8),
        Arguments.of((1L << 56) - 1, 8),
        Arguments.of(1L << 56, 9),
        Arguments.of(Long.MAX_VALUE, 9),
        Arguments.of(Long.MIN_VALUE, 10),
        Arguments.of(-1L, 10));
  }

  @ParameterizedTest
  @MethodSource("varUnsignedLongProvider")
  void putVarLongUnchecked(long unsignedLong, int nrBytesWritten) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(nrBytesWritten)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarLong(unsignedLong);
      memBuffer.flip();

      assertEquals(nrBytesWritten, memBuffer.getLimit());

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(unsignedLong, memDecoder.getVarLong());
    }
  }

  @Test
  void putVarLongUncheckedMultiple() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(100)) {
      MemoryEncoder memEncoder = MemoryEncoder.wrap(memBuffer);
      memEncoder.putVarLong(0b1111111_11111111L);
      memEncoder.putVarLong(0b1111111_11111111L);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(0b1111111_11111111L, memDecoder.getVarLong());
      assertEquals(0b1111111_11111111L, memDecoder.getVarLong());
    }
  }
}
