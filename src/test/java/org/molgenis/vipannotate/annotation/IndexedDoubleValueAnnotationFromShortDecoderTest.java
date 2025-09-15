package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.fory.memory.MemoryBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class IndexedDoubleValueAnnotationFromShortDecoderTest {
  @Mock private DoubleCodec doubleCodec;
  @Mock private DoubleInterval doubleInterval;
  private IndexedDoubleValueAnnotationFromShortDecoder indexedDoubleValueAnnotationFromShortDecoder;

  @BeforeEach
  void setUp() {
    indexedDoubleValueAnnotationFromShortDecoder =
        new IndexedDoubleValueAnnotationFromShortDecoder(doubleCodec, doubleInterval);
  }

  @Test
  void decode() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    when(memoryBuffer.getInt16(4)).thenReturn((short) 123);
    when(doubleCodec.decodeDoubleFromShort((short) 123, doubleInterval)).thenReturn(3d);
    assertEquals(
        new DoubleValueAnnotation(3d),
        indexedDoubleValueAnnotationFromShortDecoder.decode(memoryBuffer, 2));
  }
}
