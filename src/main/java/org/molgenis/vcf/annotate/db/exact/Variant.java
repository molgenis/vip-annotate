package org.molgenis.vcf.annotate.db.exact;

/**
 * Sequence variant
 *
 * @param contig reference genome contig identifier
 * @param start reference genome start position (inclusive, 1-based)
 * @param stop reference genome stop position (inclusive, 1-based)
 * @param alt alternative allele bases [A,C,G,T]
 * @see <a href="http://sequenceontology.org/browser/current_release/term/SO:0001060">SO:0001060</a>
 */
public record Variant(String contig, int start, int stop, byte[] alt) {
  /**
   * @return number of reference allele bases
   */
  public int getRefLength() {
    return stop - start + 1;
  }

  /**
   * @return number of alternate allele bases
   */
  public int getAltLength() {
    return alt.length;
  }
}
