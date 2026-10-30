package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.SizedIterator;

class EnumAnnotationDatasetEncoderTest {
  private EnumAnnotationDatasetEncoder datasetEncoder;

  @BeforeEach
  void setUp() {
    datasetEncoder =
        new EnumAnnotationDatasetEncoder(
            new EnumAnnotationSpec(null, false, new String[] {"one", "two", "three"}));
  }

  private static Stream<Arguments> encodeIntoTestProvider() {
    return Stream.of(
        arguments(
            List.of("one", "two", "three", "three", "two", "one"),
            new byte[] {(byte) 0b10_10_01_00, (byte) 0b00_01}),
        arguments(List.of("one", "two", "three", "two"), new byte[] {(byte) 0b01_10_01_00}));
  }

  @ParameterizedTest
  @MethodSource("encodeIntoTestProvider")
  void encodeInto(List<String> values, byte[] expectedBytes) {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringAnnotation> annotations = values.stream().map(StringAnnotation::new).toList();

    datasetEncoder.encodeInto(
        new SizedIterator<>(annotations.iterator(), annotations.size()), memEncoderProvider);

    for (byte expectedByte : expectedBytes) {
      verify(memEncoder).putByte(expectedByte);
    }
    verifyNoMoreInteractions(memEncoder);
  }

  @Test
  void encodeIntoInvalidValue() {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringAnnotation> annotations = Stream.of("four").map(StringAnnotation::new).toList();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            datasetEncoder.encodeInto(
                new SizedIterator<>(annotations.iterator(), annotations.size()),
                memEncoderProvider));
  }

  @Test
  void encodeIntoInvalidValueNull() {
    EnumAnnotationDatasetEncoder datasetEncoder =
        new EnumAnnotationDatasetEncoder(
            new EnumAnnotationSpec(null, false, new String[] {"one", "two", "three"}));

    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringAnnotation> annotations =
        Stream.of((String) null).map(StringAnnotation::new).toList();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            datasetEncoder.encodeInto(
                new SizedIterator<>(annotations.iterator(), annotations.size()),
                memEncoderProvider));
  }

  @Test
  void encodeIntoEnumCrossesByteBoundary() {
    EnumAnnotationDatasetEncoder datasetEncoder =
        new EnumAnnotationDatasetEncoder(
            new EnumAnnotationSpec(
                null, false, new String[] {"one", "two", "three", "four", "five"}));

    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringAnnotation> annotations =
        Stream.of("one", "two", "three", "four", "five").map(StringAnnotation::new).toList();

    datasetEncoder.encodeInto(
        new SizedIterator<>(annotations.iterator(), annotations.size()), memEncoderProvider);

    verify(memEncoder).putByte((byte) 0b10_001_000);
    verify(memEncoder).putByte((byte) 0b100_011_0);
    verifyNoMoreInteractions(memEncoder);
  }

  @Test
  void encodeIntoExactByteBoundary() {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringAnnotation> annotations =
        Stream.of("one", "two", "three", "two").map(StringAnnotation::new).toList();

    datasetEncoder.encodeInto(
        new SizedIterator<>(annotations.iterator(), annotations.size()), memEncoderProvider);

    verify(memEncoder).putByte((byte) 0b01_10_01_00);
    verifyNoMoreInteractions(memEncoder);
  }

  @Test
  void encodeSingleEnumValue() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new EnumAnnotationDatasetEncoder(
                new EnumAnnotationSpec(null, false, new String[] {"one"})));
  }
}
