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
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IndexRange;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class IndexedDoubleValueAnnotationToShortEncoderTest {
  @Mock private DoubleCodec doubleCodec;
  private IndexedDoubleValueAnnotationToShortEncoder indexedDoubleValueAnnotationToShortEncoder;

  @BeforeEach
  void setUp() {
    indexedDoubleValueAnnotationToShortEncoder =
        new IndexedDoubleValueAnnotationToShortEncoder(doubleCodec, new DoubleInterval(-1d, 1d));
  }

  @Test
  void getAnnotationSizeInBytes() {
    assertEquals(2, indexedDoubleValueAnnotationToShortEncoder.getAnnotationSizeInBytes());
  }

  @Test
  void encode() {
    IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation =
        new IndexedAnnotation<>(2, new DoubleValueAnnotation(3d));

    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    when(doubleCodec.encodeDoubleAsShort(3d, -1, 1d)).thenReturn((short) 123);
    indexedDoubleValueAnnotationToShortEncoder.encode(indexedAnnotation, memoryBuffer);
    verify(memoryBuffer).putInt16(4, (short) 123);
  }

  @Test
  void clear() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    when(doubleCodec.encodeDoubleAsShort(null, -1, 1d)).thenReturn((short) 123);
    indexedDoubleValueAnnotationToShortEncoder.clear(new IndexRange(2, 4), memoryBuffer);
    verify(memoryBuffer).putInt16(4, (short) 123);
    verify(memoryBuffer).putInt16(6, (short) 123);
    verifyNoMoreInteractions(memoryBuffer);
  }
}
