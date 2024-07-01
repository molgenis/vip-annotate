package org.molgenis.vipannotate.annotation;

public enum SequenceVariantType {
  /**
   * Single nucleotide variant.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0001483">SO:0001483</a>
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:1000002">SO:1000002</a>
   */
  SNV,
  /**
   * Multiple nucleotide variant.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0002007">SO:0002007</a>
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:1000002">SO:1000002</a>
   */
  MNV,
  /**
   * A sequence alteration which included an insertion and a deletion, affecting two or more bases
   * and which is not a substitution.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:1000032">SO:1000032</a>
   * @see <a href="https://hgvs-nomenclature.org/recommendations/DNA/delins/">HGVS Nomenclature</a>
   */
  INDEL,
  /**
   * Insertion of one or more bases.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0000667">SO:0000667</a>
   */
  INSERTION,
  /**
   * Deletion of one or more bases.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0000159">SO:0000159</a>
   */
  DELETION,
  /**
   * Structural alteration.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0001785">SO:0001785</a>
   */
  STRUCTURAL,
  /**
   * Sequence variants that do not match other types such as 'missing value', 'allele missing due to
   * overlapping deletion' and 'the unspecified allele'.
   */
  OTHER
}
