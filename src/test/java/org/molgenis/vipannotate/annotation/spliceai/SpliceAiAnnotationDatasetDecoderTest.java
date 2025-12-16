package org.molgenis.vipannotate.annotation.spliceai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class SpliceAiAnnotationDatasetDecoderTest {
  private DoubleCodec doubleCodec;
  private SpliceAiAnnotationDatasetDecoder spliceAiAnnotationDatasetDecoder;

  @BeforeEach
  void setUp() {
    doubleCodec = mock(DoubleCodec.class);
    spliceAiAnnotationDatasetDecoder = new SpliceAiAnnotationDatasetDecoder(doubleCodec);
  }

  @SuppressWarnings({"DataFlowIssue", "NullAway"})
  @Test
  void decodeScore() {
    when(doubleCodec.decodeDoublePrimitiveUnitIntervalFromByte((byte) 2)).thenReturn(0.5d);
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[] {1, 2, 3});
    assertEquals(0.5d, spliceAiAnnotationDatasetDecoder.decodeScore(memoryBuffer, 1), 1E-6);
  }

  @Test
  void decodePos() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[] {25, 50, 75});
    assertEquals((byte) -1, spliceAiAnnotationDatasetDecoder.decodePos(memoryBuffer, 1));
  }

  @Test
  void decodePosZero() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[] {25, 51, 75});
    assertEquals((byte) 0, (byte) spliceAiAnnotationDatasetDecoder.decodePos(memoryBuffer, 1));
  }

  @Test
  void decodePosNull() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[] {25, 0, 75});
    assertNull(spliceAiAnnotationDatasetDecoder.decodePos(memoryBuffer, 1));
  }
}
