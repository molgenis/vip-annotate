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
class IntAnnotationEncoderTest {
  @Mock private IntValueWriter valueWriter;
  private IntAnnotationEncoder annotationEncoder;

  @BeforeEach
  void setUp() {
    annotationEncoder = new IntAnnotationEncoder(valueWriter);
  }

  @Test
  void encodeInto() {
    IntAnnotation annotation = when(mock(IntAnnotation.class).getValue()).thenReturn(4L).getMock();
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    annotationEncoder.encodeInto(annotation, memEncoder);
    verify(valueWriter).write(4L, memEncoder);
  }

  @Test
  void getEncodedSizeInBytes() {
    when(valueWriter.getValueSizeInBytes()).thenReturn(3);
    assertEquals(3L, annotationEncoder.getEncodedSizeInBytes());
  }
}
