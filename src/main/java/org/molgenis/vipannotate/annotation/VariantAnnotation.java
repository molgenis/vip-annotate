package org.molgenis.vipannotate.annotation;

/**
 * Alternate non-reference allele annotations.
 *
 * @param variant alternate non-reference allele
 * @param annotation annotation
 */
public record VariantAnnotation<T>(Variant variant, T annotation) implements LocusAnnotation<T> {
  @Override
  public String contig() {
    return variant.contig();
  }

  @Override
  public int start() {
    return variant.start();
  }

  @Override
  public int stop() {
    return variant.stop();
  }
}
