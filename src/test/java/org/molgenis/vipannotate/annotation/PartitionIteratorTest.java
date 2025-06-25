package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class PartitionIteratorTest {
  @Test
  void hasNextAndNext() {
    hasNextAndNext(false);
  }

  @Test
  void hasNextAndNextReusable() {
    hasNextAndNext(true);
  }

  private void hasNextAndNext(boolean reusable) {
    Contig chr1 = new Contig("chr1", 248956422);
    Contig chr2 = new Contig("chr2", 242193529);

    Interval interval0 = new Interval(chr1, 1, (1 << 20) + 1);
    AnnotatedInterval<Interval, Annotation> annotatedInterval0 =
        new AnnotatedInterval<>(interval0, mock(Annotation.class));

    Interval interval1 = new Interval(chr1, 2, 3);
    AnnotatedInterval<Interval, Annotation> annotatedInterval1 =
        new AnnotatedInterval<>(interval1, mock(Annotation.class));

    Interval interval2 = new Interval(chr1, (1 << 20), (1 << 20) + 1); // boundary check
    AnnotatedInterval<Interval, Annotation> annotatedInterval2 =
        new AnnotatedInterval<>(interval2, mock(Annotation.class));

    Interval interval3 = new Interval(chr1, (1 << 20) + 1, (1 << 20) + 2);
    AnnotatedInterval<Interval, Annotation> annotatedInterval3 =
        new AnnotatedInterval<>(interval3, mock(Annotation.class));

    Interval interval4 = new Interval(chr2, 2, 3);
    AnnotatedInterval<Interval, Annotation> annotatedInterval4 =
        new AnnotatedInterval<>(interval4, mock(Annotation.class));

    List<AnnotatedInterval<Interval, Annotation>> annotatedIntervals =
        List.of(
            annotatedInterval0,
            annotatedInterval1,
            annotatedInterval2,
            annotatedInterval3,
            annotatedInterval4);
    PartitionIterator<Interval, Annotation, AnnotatedInterval<Interval, Annotation>> it;
    if (reusable) {
      List<AnnotatedInterval<Interval, Annotation>> reusableAnnotatedIntervals = new ArrayList<>();
      it = new PartitionIterator<>(annotatedIntervals.iterator(), reusableAnnotatedIntervals);
    } else {
      it = new PartitionIterator<>(annotatedIntervals.iterator());
    }
    assertTrue(it.hasNext());
    assertEquals(
        new Partition<>(
            new Partition.Key(chr1, 0), List.of(annotatedInterval0, annotatedInterval1)),
        it.next());
    assertTrue(it.hasNext());
    assertEquals(
        new Partition<>(
            new Partition.Key(chr1, 1), List.of(annotatedInterval2, annotatedInterval3)),
        it.next());
    assertTrue(it.hasNext());
    assertEquals(
        new Partition<>(new Partition.Key(chr2, 0), List.of(annotatedInterval4)), it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void nextNoSuchElementException() {
    PartitionIterator<Interval, Annotation, AnnotatedInterval<Interval, Annotation>> it =
        new PartitionIterator<>(Collections.emptyIterator());
    assertThrows(NoSuchElementException.class, it::next);
  }
}
