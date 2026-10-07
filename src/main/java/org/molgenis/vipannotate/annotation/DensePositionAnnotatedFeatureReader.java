package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.spec.PositionBinSpec;

@RequiredArgsConstructor
public final class DensePositionAnnotatedFeatureReader implements AnnotatedFeatureReader {
  private final AnnotatedFeatureReader delegate;
  private final Annotation nullAnnotation;
  private final Map<String, PositionBinSpec> partitioningSpec;

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

    if (buffered == null) {
      return emitNextMissingPosition();
    }

    if (lastPosition == null) {
      Position actual = buffered.getFeature();
      validatePosition(actual);

      PositionBinSpec spec = getPositionBinSpec(actual.getContig());

      long actualZeroBased = actual.getStart() - 1L;
      if (actualZeroBased > spec.offset()) {
        next = nullPosition(actual.getContig(), spec.offset());
      } else {
        next = buffered;
        buffered = null;
      }

      return true;
    }

    Position last = lastPosition;
    Position actual = buffered.getFeature();

    if (!last.getContig().equals(actual.getContig())) {
      // Finish the previous contig before moving to the next one.
      if (emitNextMissingPosition()) {
        return true;
      }

      // The previous contig is complete. Process the new contig.
      lastPosition = null;
      return loadNext();
    }

    validatePosition(actual);

    long expected = (long) last.getStart() + 1;
    long actualStart = actual.getStart();

    if (actualStart < expected) {
      throw new IllegalArgumentException(
          "Positions are not strictly increasing: " + last + " followed by " + actual);
    }

    if (actualStart > expected) {
      next = nullPosition(last.getContig(), expected);
      return true;
    }

    // The real position is exactly the next expected position.
    next = buffered;
    buffered = null;
    return true;
  }

  /**
   * Emits the next missing position in the current contig, if its configured dense range has not
   * yet been completed.
   */
  private boolean emitNextMissingPosition() {
    if (lastPosition == null) {
      return false;
    }

    Contig contig = lastPosition.getContig();
    PositionBinSpec spec = getPositionBinSpec(contig);
    long expected = (long) lastPosition.getStart() + 1;

    if (expected <= spec.endExclusive()) {
      next = nullPosition(contig, expected);
      return true;
    }

    return false;
  }

  private void validatePosition(Position position) {
    PositionBinSpec spec = getPositionBinSpec(position.getContig());
    long start = position.getStart() - 1;

    if (start < spec.offset() || start >= spec.endExclusive()) {
      throw new IllegalArgumentException(
          "Position %d is outside configured range [%d, %d)"
              .formatted(start, spec.offset(), spec.endExclusive()));
    }
  }

  private PositionBinSpec getPositionBinSpec(Contig contig) {
    PositionBinSpec spec = partitioningSpec.get(contig.getName());
    if (spec == null) {
      throw new IllegalStateException();
    }
    return spec;
  }

  private AnnotatedPosition<Annotation> nullPosition(Contig contig, long position) {
    return new AnnotatedPosition<>(new Position(contig, Math.toIntExact(position)), nullAnnotation);
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
