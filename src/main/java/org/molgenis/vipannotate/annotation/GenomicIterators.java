package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public final class GenomicIterators {
  private GenomicIterators() {}

  public static <T extends Annotation>
      Function<AnnotatedInterval<Interval, T>, Iterator<AnnotatedInterval<Position, T>>>
          iteratePositions() {

    return annotatedInterval -> {
      Interval interval = annotatedInterval.getFeature();
      T annotation = annotatedInterval.getAnnotation();
      Contig contig = interval.getContig();

      return new Iterator<>() {
        int pos = interval.getStart();

        @Override
        public boolean hasNext() {
          return pos <= interval.getStop();
        }

        @Override
        public AnnotatedInterval<Position, T> next() {
          if (!hasNext()) throw new NoSuchElementException();

          return new AnnotatedInterval<>(new Position(contig, pos++), annotation);
        }
      };
    };
  }
}
