package org.molgenis.vipannotate.annotation.spliceai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.SizedIterator;

class SpliceAiAnnotationDatasetEncoderTest {
  private DoubleCodec doubleCodec;
  private SpliceAiAnnotationDatasetEncoder spliceAiAnnotationDatasetEncoder;

  @BeforeEach
  void setUp() {
    doubleCodec = mock(DoubleCodec.class);
    spliceAiAnnotationDatasetEncoder = new SpliceAiAnnotationDatasetEncoder(doubleCodec);
  }

  @Test
  void encodeScore() {
    doReturn((byte) 1).when(doubleCodec).encodeDoublePrimitiveUnitIntervalAsByte(0d);
    doReturn((byte) 2).when(doubleCodec).encodeDoublePrimitiveUnitIntervalAsByte(0.5d);
    doReturn((byte) 3).when(doubleCodec).encodeDoublePrimitiveUnitIntervalAsByte(1d);

    List<Double> scores = List.of(0d, 0.5d, 1d);
    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[3])) {
      spliceAiAnnotationDatasetEncoder.encodeScore(
          new SizedIterator<>(scores.iterator(), scores.size()), memBuffer);
      assertAll(
          () -> assertEquals((byte) 1, memBuffer.getByteAtIndex(0)),
          () -> assertEquals((byte) 2, memBuffer.getByteAtIndex(1)),
          () -> assertEquals((byte) 3, memBuffer.getByteAtIndex(2)));
    }
  }

  @Test
  void encodePos() {
    List<@Nullable Byte> positions = new ArrayList<>();
    positions.add(null);
    positions.add((byte) -25);
    positions.add((byte) 0);
    positions.add((byte) 25);

    try (MemoryBuffer memBuffer = MemoryBuffer.wrap(new byte[4])) {
      spliceAiAnnotationDatasetEncoder.encodePos(
          new SizedIterator<>(positions.iterator(), positions.size()), memBuffer);
      assertAll(
          () -> assertEquals((byte) 0, memBuffer.getByteAtIndex(0)),
          () -> assertEquals((byte) 26, memBuffer.getByteAtIndex(1)),
          () -> assertEquals((byte) 51, memBuffer.getByteAtIndex(2)),
          () -> assertEquals((byte) 76, memBuffer.getByteAtIndex(3)));
    }
  }
}
