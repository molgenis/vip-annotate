package org.molgenis.vipannotate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.annotation.Contig;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class RegionTest {
  private static Stream<Arguments> validRegionArgsProvider() {
    return Stream.of(
        Arguments.of(new Contig("chr1"), 1, 2),
        Arguments.of(new Contig("chr1"), 1, null),
        Arguments.of(new Contig("chr1"), null, 2),
        Arguments.of(new Contig("chr1", 3), 1, 2),
        Arguments.of(new Contig("chr1", 3), 1, 3),
        Arguments.of(new Contig("chr1"), 2, 2));
  }

  private static Stream<Arguments> invalidRegionArgsProvider() {
    return Stream.of(
        Arguments.of(new Contig("chr1"), 0, null),
        Arguments.of(new Contig("chr1"), null, 0),
        Arguments.of(new Contig("chr1"), 0, 1),
        Arguments.of(new Contig("chr1"), 2, 1),
        Arguments.of(new Contig("chr1", 3), 1, 4),
        Arguments.of(new Contig("chr1", 3), null, 4));
  }

  @ParameterizedTest
  @MethodSource("validRegionArgsProvider")
  void validRegion(Contig contig, @Nullable Integer start, @Nullable Integer stop) {
    assertDoesNotThrow(() -> new Region(contig, start, stop));
  }

  @ParameterizedTest
  @MethodSource("invalidRegionArgsProvider")
  void invalidRegion(Contig contig, @Nullable Integer start, @Nullable Integer stop) {
    assertThrows(IllegalArgumentException.class, () -> new Region(contig, start, stop));
  }
}
