package org.molgenis.vipannotate.format.fasta;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

/**
 * FASTA index record, see <a
 * href="https://www.htslib.org/doc/faidx.html">https://www.htslib.org/doc/faidx.html</a>.
 *
 * @param name Name of this reference sequence.
 * @param length Total length of this reference sequence, in bases.
 * @param offset Offset in the FASTA file of this sequence's first base.
 * @param lineBases The number of bases on each line.
 * @param lineWidth The number of bytes in each line, including the newline.
 */
public record FastaIndexRecord(String name, int length, long offset, int lineBases, int lineWidth) {
  public FastaIndexRecord {
    validateNonNegative(length);
    validateNonNegative(offset);
    validateNonNegative(lineBases);
    validateNonNegative(lineWidth);
  }
}
