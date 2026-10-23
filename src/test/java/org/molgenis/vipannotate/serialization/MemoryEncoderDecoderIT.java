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
      MemoryEncoder memEncoder = MemoryEncoder.fixed(memBuffer);
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
      MemoryEncoder memEncoder = MemoryEncoder.autoGrowing(memBuffer);
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
      MemoryEncoder memEncoder = MemoryEncoder.fixed(memBuffer);
      memEncoder.putVarInt(unsignedInt);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(unsignedInt, memDecoder.getVarInt());
    }
  }

  @Test
  void putVarIntUncheckedMultiple() {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocateHeap(100)) {
      MemoryEncoder memEncoder = MemoryEncoder.fixed(memBuffer);
      memEncoder.putVarInt(0b1111111_11111111);
      memEncoder.putVarInt(0b1111111_11111111);
      memBuffer.flip();

      MemoryDecoder memDecoder = MemoryDecoder.wrap(memBuffer);
      assertEquals(0b1111111_11111111, memDecoder.getVarInt());
      assertEquals(0b1111111_11111111, memDecoder.getVarInt());
    }
  }
}
