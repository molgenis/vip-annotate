package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@ExtendWith(MockitoExtension.class)
class OffsetIntAnnotationEncoderTest {
  @Mock private IntValueWriter intValueWriter;
  private OffsetIntAnnotationEncoder intAnnotationEncoder;

  @BeforeEach
  void setUp() {
    intAnnotationEncoder = new OffsetIntAnnotationEncoder(intValueWriter, 2);
  }

  @Test
  void encodeInto() {
    IntAnnotation intAnnotation =
        when(mock(IntAnnotation.class).getValue()).thenReturn(4L).getMock();
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    intAnnotationEncoder.encodeInto(intAnnotation, memEncoder);
    verify(intValueWriter).write(6L, memEncoder);
  }

  @Test
  void getEncodedSizeInBytes() {
    when(intValueWriter.getValueSizeInBytes()).thenReturn(3);
    assertEquals(3L, intAnnotationEncoder.getEncodedSizeInBytes());
  }
}
