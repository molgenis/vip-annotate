package org.molgenis.vipannotate.annotation.spliceai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.apache.fury.memory.MemoryBuffer;
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
    List<Byte> positions = List.of((byte) -25, (byte) 0, (byte) 25);
    MemoryBuffer memoryBuffer =
        spliceAiAnnotationDatasetEncoder.encodePos(
            new SizedIterator<>(positions.iterator(), positions.size()));
    assertArrayEquals(new byte[] {25, 50, 75}, memoryBuffer.getArray());
  }
}
