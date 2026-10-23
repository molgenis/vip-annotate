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
class FloatAnnotationEncoderTest {
  @Mock private FloatValueWriter valueWriter;
  private FloatAnnotationEncoder annotationEncoder;

  @BeforeEach
  void setUp() {
    annotationEncoder = new FloatAnnotationEncoder(valueWriter);
  }

  @Test
  void encodeInto() {
    FloatAnnotation annotation =
        when(mock(FloatAnnotation.class).getValue()).thenReturn(1.23).getMock();
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    annotationEncoder.encodeInto(annotation, memEncoder);
    verify(valueWriter).write(1.23, memEncoder);
  }

  @Test
  void getEncodedSizeInBytes() {
    when(valueWriter.getValueSizeInBytes()).thenReturn(3);
    assertEquals(3, annotationEncoder.getEncodedSizeInBytes());
  }
}
