package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.BedInputFormat;
import org.molgenis.vipannotate.util.Input;

@RequiredArgsConstructor
public class BedAnnotatedFeatureReader implements AnnotatedFeatureReader {
  private final Input input;
  private final BedInputFormat bedInputFormat;
  private final ResolvedAnnotationSpecs annotationSpecs;

  @Override
  public boolean hasNext() {
    throw new UnsupportedOperationException(); // FIXME implement
  }

  @Override
  public AnnotatedFeature<?, ?> next() {
    throw new UnsupportedOperationException(); // FIXME implement
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException(); // FIXME implement
  }

  /*
    private Iterator<AnnotatedInterval<Position, ScalarAnnotation>> createAnnotatedPosIteratorFromBed(
      Iterator<BedFeature> bedFeatureIterator, BedInputFormat bedInputFormat) {
    // FIXME bed support
    throw new UnsupportedOperationException();
    //    return Iterators.flatMap(
    //        Iterators.map(bedFeatureIterator, bedFeature -> create(bedFeature,
    // bedInputFormat.from())),
    //        GenomicIterators.iteratePositions());
  }

  // TODO improve performance by reusing annotated interval
  // TODO improve performance by reusing contig
  private AnnotatedInterval<Interval, ScalarAnnotation> create(
      BedFeature bedFeature, BedField from) {
    Contig contig = new Contig(bedFeature.getChrom().get().toString(), 9); // FIXME hardcoded
    int start = bedFeature.getChromStart().get();
    int end = bedFeature.getChromEnd().get();
    if (end - start == 0) {
      // source: https://samtools.github.io/hts-specs/BEDv1.pdf
      // If chromEnd is equal to chromStart, this indicates a feature between chromStart and the
      // preceding base, such as an insertion.
      throw new UnsupportedOperationException();
    }

    Interval interval;
    if (end - start == 1) {
      // 1-based inclusive
      interval = new Position(contig, start + 1);
    } else {
      // [1-based inclusive, 1-based inclusive]
      interval = new Interval(contig, start + 1, end);
    }
    if (from.getColIndex() == 3) {
      // FIXME get value type from spec, support other things then double
      return new AnnotatedInterval<>(
          interval,
          new DoubleAnnotation(Double.parseDouble(bedFeature.getName().get().toString())));
    }
    throw new RuntimeException("not implemented"); // FIXME support data in other cols e.g. score
  }
   */
}
