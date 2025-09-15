package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.fory.memory.MemoryBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.IndexRange;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class IndexedDoubleValueAnnotationToByteEncoderTest {
  @Mock private DoubleCodec doubleCodec;
  private IndexedDoubleValueAnnotationToByteEncoder indexedDoubleValueAnnotationToByteEncoder;

  @BeforeEach
  void setUp() {
    indexedDoubleValueAnnotationToByteEncoder =
        new IndexedDoubleValueAnnotationToByteEncoder(doubleCodec, -1d, 1d);
  }

  @Test
  void getAnnotationSizeInBytes() {
    assertEquals(1, indexedDoubleValueAnnotationToByteEncoder.getAnnotationSizeInBytes());
  }

  @Test
  void encode() {
    IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation =
        new IndexedAnnotation<>(2, new DoubleValueAnnotation(3d));

    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    when(doubleCodec.encodeDoubleAsByte(3d, -1, 1d)).thenReturn((byte) 123);
    indexedDoubleValueAnnotationToByteEncoder.encode(indexedAnnotation, memoryBuffer);
    verify(memoryBuffer).putByte(2, (byte) 123);
  }

  @Test
  void clear() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    when(doubleCodec.encodeDoubleAsByte(null, -1, 1d)).thenReturn((byte) 123);
    indexedDoubleValueAnnotationToByteEncoder.clear(new IndexRange(2, 4), memoryBuffer);
    verify(memoryBuffer).putByte(2, (byte) 123);
    verify(memoryBuffer).putByte(3, (byte) 123);
    verifyNoMoreInteractions(memoryBuffer);
  }
}
