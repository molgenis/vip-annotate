package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class QualTest {
  private static Stream<Arguments> getArgsProvider() {
    return Stream.of(Arguments.of("1", 1d), Arguments.of("1.23", 1.23d));
  }

  @ParameterizedTest
  @MethodSource("getArgsProvider")
  void get(String fieldRaw, double doubleValue) {
    Double qualValue = Qual.wrap(fieldRaw).get();
    assertNotNull(qualValue);
    assertEquals(doubleValue, qualValue, 1E-6);
  }

  @Test
  void getMissing() {
    assertNull(Qual.wrap(".").get());
  }

  @Test
  void testToString() {
    assertEquals("QUAL=1.23", Qual.wrap("1.23").toString());
  }
}
