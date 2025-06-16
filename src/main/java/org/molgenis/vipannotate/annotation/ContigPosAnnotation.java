package org.molgenis.vipannotate.annotation;

import lombok.NonNull;

/**
 * @param contig contig identifier
 * @param pos 1-based start position
 * @param score
 */
public record ContigPosAnnotation(String contig, int pos, @NonNull Double score)
    implements LocusAnnotation<Double> {
  @Override
  public int start() {
    return pos;
  }

  @Override
  public int stop() {
    return pos;
  }

  @Override
  public Double annotation() {
    return score;
  }
}
