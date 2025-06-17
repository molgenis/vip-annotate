package org.molgenis.vipannotate.util;

import lombok.NonNull;

/**
 * @param name Name of this reference sequence.
 * @param length Total length of this reference sequence, in bases.
 * @param offset Offset in the FASTA file of this sequence's first base.
 * @param lineBases The number of bases on each line.
 * @param lineWidth The number of bytes in each line, including the newline.
 */
public record FastaIndexRecord(
    @NonNull String name, long length, long offset, int lineBases, int lineWidth) {
  public FastaIndexRecord {
    ParameterValidation.requireNonNegative(length);
    ParameterValidation.requireNonNegative(offset);
    ParameterValidation.requireNonNegative(lineBases);
    ParameterValidation.requireNonNegative(lineWidth);
  }
}
