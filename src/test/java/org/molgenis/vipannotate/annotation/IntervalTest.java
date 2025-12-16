package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class IntervalTest {
  @Test
  void create() {
    Contig contig = mock(Contig.class);
    Interval interval = new Interval(contig, 1, 2);
    assertAll(
        () -> assertEquals(contig, interval.getContig()),
        () -> assertEquals(1, interval.getStart()),
        () -> assertEquals(2, interval.getStop()));
  }

  @Test
  void createIntervalInvalidStart() {
    Contig contig = mock(Contig.class);
    assertThrows(IllegalArgumentException.class, () -> new Interval(contig, -1, 1));
  }

  @Test
  void createIntervalInvalidStopLowerThanStart() {
    Contig contig = mock(Contig.class);
    assertThrows(IllegalArgumentException.class, () -> new Interval(contig, 2, 1));
  }
}
