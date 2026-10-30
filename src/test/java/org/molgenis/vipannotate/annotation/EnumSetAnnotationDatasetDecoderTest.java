package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

class EnumSetAnnotationDatasetDecoderTest {
  private EnumSetAnnotationDatasetDecoder datasetDecoder;

  @BeforeEach
  void setUp() {
    datasetDecoder =
        new EnumSetAnnotationDatasetDecoder(
            new EnumSetAnnotationSpec(null, false, new String[] {"one", "two", "three"}));
  }

  private static Stream<Arguments> decodeProvider() {
    return Stream.of(
        Arguments.of(
            List.of(List.of("one"), List.of("two"), List.of("one", "three")),
            new byte[] {(byte) 0b01_010_001, (byte) 0b1}),
        Arguments.of(
            List.of(
                List.of("one"),
                List.of("one"),
                List.of("one"),
                List.of("one"),
                List.of("one"),
                List.of("one"),
                List.of("one"),
                List.of("one")),
            new byte[] {(byte) 0b01_001_001, (byte) 0b1_001_001_0, (byte) 0b001_001_00}));
  }

  @ParameterizedTest
  @MethodSource("decodeProvider")
  void decode(List<List<String>> expectedValues, byte[] encodedBytes) {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    MemoryDecoderProvider decoderProvider = () -> memDecoder;

    for (int i = 0; i < encodedBytes.length; i++) {
      when(memDecoder.getUnsignedByteAtIndex(i)).thenReturn(encodedBytes[i] & 0xFF);
    }

    AnnotationDataset<StringListAnnotation> dataset = datasetDecoder.decode(decoderProvider);

    assertAll(
        IntStream.range(0, expectedValues.size())
            .mapToObj(
                i ->
                    () -> {
                      StringListAnnotation annotation =
                          dataset.findByIndexInto(i, StringListAnnotation::new);
                      assertNotNull(annotation);
                      assertEquals(expectedValues.get(i), annotation.getValues());
                    }));
  }

  @Test
  void decodeEmptyAnnotation() {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    MemoryDecoderProvider decoderProvider = () -> memDecoder;

    AnnotationDataset<StringListAnnotation> dataset = datasetDecoder.decode(decoderProvider);

    StringListAnnotation annotation = dataset.findByIndexInto(0, StringListAnnotation::new);

    assertNotNull(annotation);
    assertTrue(annotation.getValues().isEmpty());
  }

  @Test
  void decodeNullMemoryDecoder() {
    MemoryDecoderProvider decoderProvider = () -> null;

    AnnotationDataset<StringListAnnotation> dataset = datasetDecoder.decode(decoderProvider);

    assertSame(EmptyAnnotationDataset.getInstance(), dataset);
  }

  @Test
  void decodeNegativeIndex() {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    MemoryDecoderProvider decoderProvider = () -> memDecoder;

    AnnotationDataset<StringListAnnotation> dataset = datasetDecoder.decode(decoderProvider);

    assertThrows(
        IllegalArgumentException.class,
        () -> dataset.findByIndexInto(-1, StringListAnnotation::new));
  }

  @Test
  void decodeEmptyEnumValues() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new EnumSetAnnotationDatasetDecoder(
                new EnumSetAnnotationSpec(null, false, new String[0])));
  }

  @Test
  void decodeTooManyEnumValues() {
    String[] enumValues = IntStream.range(0, 32).mapToObj(i -> "value" + i).toArray(String[]::new);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            new EnumSetAnnotationDatasetDecoder(
                new EnumSetAnnotationSpec(null, false, enumValues)));
  }
}
