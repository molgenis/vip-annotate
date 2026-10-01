package org.molgenis.vipannotate.annotation;

import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class DensePositionAnnotatedFeatureReader implements AnnotatedFeatureReader {
  /** 1st base has position 1, but telomeres is 0 or @link{{@link Contig#getLength()}} + 1 */
  private static final int FIRST_POSITION = 0;

  private final AnnotatedFeatureReader delegate;
  private final Annotation nullAnnotation;

  @Nullable private AnnotatedPosition<?> buffered;
  @Nullable private AnnotatedPosition<?> next;
  @Nullable private Position lastPosition;

  @Override
  public boolean hasNext() {
    return loadNext();
  }

  @Override
  public AnnotatedFeature<?, ?> next() {
    if (!loadNext()) {
      throw new NoSuchElementException();
    }

    AnnotatedPosition<?> result = Objects.requireNonNull(next);
    next = null;
    lastPosition = result.getFeature();
    return result;
  }

  private boolean loadNext() {
    if (next != null) {
      return true;
    }

    if (buffered == null && delegate.hasNext()) {
      buffered = asPosition(delegate.next());
    }

    // No input left.
    if (buffered == null) {
      if (lastPosition == null) {
        return false;
      }

      int expected = lastPosition.getStart() + 1;

      if (expected <= lastPosition.getContig().getLength()) {
        next = nullPosition(lastPosition.getContig(), expected);
        return true;
      }

      return false;
    }

    // First real position.
    if (lastPosition == null) {
      Position actual = buffered.getFeature();

      if (actual.getStart() > FIRST_POSITION) {
        next = nullPosition(actual.getContig(), FIRST_POSITION);
        return true;
      }

      next = buffered;
      buffered = null;
      return true;
    }

    Position last = lastPosition;
    Position actual = buffered.getFeature();

    if (!last.getContig().equals(actual.getContig())) {
      // Finish the previous contig before moving to the next one.
      int expected = last.getStart() + 1;

      if (expected <= last.getContig().getLength()) {
        next = nullPosition(last.getContig(), expected);
        return true;
      }

      // Previous contig is complete; now emit the first position
      // of the next contig (or its leading null positions).
      lastPosition = null;
      return loadNext();
    }

    int expected = last.getStart() + 1;
    int actualStart = actual.getStart();

    if (actualStart < expected) {
      throw new IllegalArgumentException(
          "Positions are not strictly increasing: " + last + " followed by " + actual);
    }

    if (actualStart > expected) {
      next = nullPosition(last.getContig(), expected);
      return true;
    }

    next = buffered;
    buffered = null;
    return true;
  }

  private AnnotatedPosition<Annotation> nullPosition(Contig contig, int position) {
    return new AnnotatedPosition<>(new Position(contig, position), nullAnnotation);
  }

  private static AnnotatedPosition<?> asPosition(AnnotatedFeature<?, ?> feature) {
    if (feature instanceof AnnotatedPosition<?> position) {
      return position;
    }

    throw new IllegalArgumentException(
        "Expected AnnotatedPosition but got " + feature.getClass().getName());
  }

  @Override
  public void close() {
    delegate.close();
  }
}
