package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

class EnumAnnotationDatasetDecoderTest {
  private EnumAnnotationDatasetDecoder datasetDecoder;

  @BeforeEach
  void setUp() {
    datasetDecoder =
        new EnumAnnotationDatasetDecoder(
            new EnumAnnotationSpec(null, false, new String[] {"one", "two", "three"}));
  }

  private static Stream<Arguments> decodeProvider() {
    return Stream.of(
        arguments(
            new byte[] {(byte) 0b10_10_01_00, (byte) 0b00_01},
            List.of("one", "two", "three", "three", "two", "one")),
        arguments(new byte[] {(byte) 0b01_10_01_00}, List.of("one", "two", "three", "two")));
  }

  @ParameterizedTest
  @MethodSource("decodeProvider")
  void decode(byte[] encodedBytes, List<String> expectedValues) {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    MemoryDecoderProvider decoderProvider = () -> memDecoder;

    for (int i = 0; i < encodedBytes.length; i++) {
      when(memDecoder.getUnsignedByteAtIndex(i)).thenReturn(encodedBytes[i] & 0xFF);
    }

    AnnotationDataset<StringAnnotation> dataset = datasetDecoder.decode(decoderProvider);

    assertAll(
        IntStream.range(0, expectedValues.size())
            .mapToObj(
                i ->
                    () ->
                        assertEquals(
                            new StringAnnotation(expectedValues.get(i)),
                            dataset.findByIndexInto(i, StringAnnotation::new))));
  }

  @Test
  void decodeEnumCrossesByteBoundary() {
    EnumAnnotationDatasetDecoder decoder =
        new EnumAnnotationDatasetDecoder(
            new EnumAnnotationSpec(
                null, false, new String[] {"one", "two", "three", "four", "five"}));

    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    MemoryDecoderProvider decoderProvider = () -> memDecoder;

    when(memDecoder.getUnsignedByteAtIndex(0)).thenReturn(0b10_001_000);
    when(memDecoder.getUnsignedByteAtIndex(1)).thenReturn(0b0100_0110);

    AnnotationDataset<StringAnnotation> dataset = decoder.decode(decoderProvider);

    List<String> expectedValues = List.of("one", "two", "three", "four", "five");
    assertAll(
        IntStream.range(0, expectedValues.size())
            .mapToObj(
                i ->
                    () ->
                        assertEquals(
                            new StringAnnotation(expectedValues.get(i)),
                            dataset.findByIndexInto(i, StringAnnotation::new))));
  }

  @Test
  void decodeNullMemoryDecoder() {
    assertSame(EmptyAnnotationDataset.getInstance(), datasetDecoder.decode(() -> null));
  }

  @Test
  void decodeNegativeIndex() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            datasetDecoder
                .decode(() -> mock(MemoryDecoder.class))
                .findByIndexInto(-1, StringAnnotation::new));
  }

  @Test
  void decodeInvalidEnumIndex() {
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    MemoryDecoderProvider decoderProvider = () -> memDecoder;

    when(memDecoder.getUnsignedByteAtIndex(0)).thenReturn(0b11); // non-existing 4th enum value

    AnnotationDataset<StringAnnotation> dataset = datasetDecoder.decode(decoderProvider);

    assertThrows(
        IllegalArgumentException.class, () -> dataset.findByIndexInto(0, StringAnnotation::new));
  }

  @Test
  void decodeSingleValueEnum() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new EnumAnnotationDatasetDecoder(
                new EnumAnnotationSpec(null, false, new String[] {"one"})));
  }
}
