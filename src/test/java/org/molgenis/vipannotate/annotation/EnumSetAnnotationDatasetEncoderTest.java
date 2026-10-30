package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.SizedIterator;

class EnumSetAnnotationDatasetEncoderTest {
  private EnumSetAnnotationDatasetEncoder datasetEncoder;

  @BeforeEach
  void setUp() {
    datasetEncoder =
        new EnumSetAnnotationDatasetEncoder(
            new EnumSetAnnotationSpec(null, false, new String[] {"one", "two", "three"}));
  }

  private static Stream<Arguments> encodeIntoProvider() {
    return Stream.of(
        Arguments.of(
            List.of(new String[] {"one"}, new String[] {"two"}, new String[] {"one", "three"}),
            new byte[] {(byte) 0b010_100_01, (byte) 0b000_000_01}),
        // fill full last byte
        Arguments.of(
            List.of(
                new String[] {"one"},
                new String[] {"one"},
                new String[] {"one"},
                new String[] {"one"},
                new String[] {"one"},
                new String[] {"one"},
                new String[] {"one"},
                new String[] {"one"}),
            new byte[] {(byte) 0b001_001_00, (byte) 0b1_001_001_0, (byte) 0b01_001_001}));
  }

  @ParameterizedTest
  @MethodSource("encodeIntoProvider")
  void encodeInto(List<String[]> values, byte[] expectedBytes) {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringListAnnotation> annotations =
        values.stream().map(StringListAnnotation::new).toList();

    datasetEncoder.encodeInto(
        new SizedIterator<>(annotations.iterator(), annotations.size()), memEncoderProvider);

    for (byte expectedByte : expectedBytes) {
      verify(memEncoder).putByte(expectedByte);
    }
    verifyNoMoreInteractions(memEncoder);
  }

  // encoding empty enum set is allowed and differs from a missing value
  @Test
  void encodeIntoEmptyAnnotation() {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringListAnnotation> annotations = List.of(new StringListAnnotation());

    datasetEncoder.encodeInto(
        new SizedIterator<>(annotations.iterator(), annotations.size()), memEncoderProvider);

    verify(memEncoder).putByte((byte) 0);
    verifyNoMoreInteractions(memEncoder);
  }

  // missing values are not allowed, should be encoded separately
  @Test
  void encodeIntoNullAnnotation() {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringListAnnotation> annotations = Stream.of((StringListAnnotation) null).toList();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            datasetEncoder.encodeInto(
                new SizedIterator<>(annotations.iterator(), annotations.size()),
                memEncoderProvider));
  }

  @Test
  void encodeIntoInvalidValue() {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
    MemoryEncoderProvider memEncoderProvider = () -> memEncoder;

    List<StringListAnnotation> annotations =
        List.of(new StringListAnnotation(new String[] {"four"}));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            datasetEncoder.encodeInto(
                new SizedIterator<>(annotations.iterator(), annotations.size()),
                memEncoderProvider));
  }

  @Test
  void encodeIntoEmptyEnumValues() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new EnumSetAnnotationDatasetEncoder(
                new EnumSetAnnotationSpec(null, false, new String[0])));
  }

  @Test
  void encodeIntoTooManyEnumValues() {
    String[] enumValues =
        Stream.iterate(0, i -> i + 1).limit(32).map(i -> "value" + i).toArray(String[]::new);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            new EnumSetAnnotationDatasetEncoder(
                new EnumSetAnnotationSpec(null, false, enumValues)));
  }
}
