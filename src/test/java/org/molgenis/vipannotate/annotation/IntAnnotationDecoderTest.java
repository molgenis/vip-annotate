package org.molgenis.vipannotate.annotation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@ExtendWith(MockitoExtension.class)
class IntAnnotationDecoderTest {
  @Mock private IntReadValueFunction valueFunction;
  private IntAnnotationDecoder annotationDecoder;

  @BeforeEach
  void setUp() {
    annotationDecoder = new IntAnnotationDecoder(valueFunction);
  }

  @Test
  void decodeInto() {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    IntAnnotation annotation = mock(IntAnnotation.class);
    when(valueFunction.apply(memDecoder, 2)).thenReturn(3L);
    annotationDecoder.decodeInto(memDecoder, 2, annotation);
    verify(annotation).reset(3L);
  }
}
