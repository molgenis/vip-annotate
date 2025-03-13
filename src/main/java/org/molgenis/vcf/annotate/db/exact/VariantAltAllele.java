package org.molgenis.vcf.annotate.db.exact;

/**
 * Alternate non-reference allele
 *
 * @param contig reference genome contig identifier
 * @param start reference genome start position (inclusive, 1-based)
 * @param stop reference genome stop position (inclusive, 1-based)
 * @param bases allele bases [A,C,G,T]
 */
public record VariantAltAllele(String contig, int start, int stop, byte[] bases) {
  public int getRefAlleleLength() {
    return stop - start + 1;
  }

  public int getAltAlleleLength() {
    return bases.length;
  }
}
