package org.molgenis.vipannotate.annotation.spliceai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.apache.fory.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @SuppressWarnings("DataFlowIssue")
  @Test
  void encodeScore() {
    doReturn((byte) 1).when(doubleCodec).encodeDoublePrimitiveUnitIntervalAsByte(0d);
    doReturn((byte) 2).when(doubleCodec).encodeDoublePrimitiveUnitIntervalAsByte(0.5d);
    doReturn((byte) 3).when(doubleCodec).encodeDoublePrimitiveUnitIntervalAsByte(1d);

    List<Double> scores = List.of(0d, 0.5d, 1d);
    MemoryBuffer memoryBuffer =
        spliceAiAnnotationDatasetEncoder.encodeScore(
            new SizedIterator<>(scores.iterator(), scores.size()));
    assertArrayEquals(new byte[] {1, 2, 3}, memoryBuffer.getArray());
  }

  @Test
  void encodePos() {
    List<@Nullable Byte> positions = new ArrayList<>();
    positions.add(null);
    positions.add((byte) -25);
    positions.add((byte) 0);
    positions.add((byte) 25);

    MemoryBuffer memoryBuffer =
        spliceAiAnnotationDatasetEncoder.encodePos(
            new SizedIterator<>(positions.iterator(), positions.size()));
    assertArrayEquals(new byte[] {0, 26, 51, 76}, memoryBuffer.getArray());
  }
}
