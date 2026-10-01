package org.molgenis.vipannotate.annotation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@ExtendWith(MockitoExtension.class)
class FloatAnnotationDecoderTest {
  @Mock private FloatReadValueFunction valueFunction;
  private FloatAnnotationDecoder annotationDecoder;

  @BeforeEach
  void setUp() {
    annotationDecoder = new FloatAnnotationDecoder(valueFunction);
  }

  @Test
  void decodeFloat() {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    FloatAnnotation annotation = mock(FloatAnnotation.class);
    when(valueFunction.apply(memDecoder, 2)).thenReturn(1.23);
    annotationDecoder.decodeInto(memDecoder, 2, annotation);
    verify(annotation).reset(1.23);
  }
}
