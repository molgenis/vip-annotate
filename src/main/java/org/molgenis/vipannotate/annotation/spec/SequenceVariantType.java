package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SequenceVariantType {
  /**
   * Single nucleotide variant.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0001483">SO:0001483</a>
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:1000002">SO:1000002</a>
   */
  @JsonProperty("snv")
  SNV,
  /**
   * Multiple nucleotide variant.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0002007">SO:0002007</a>
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:1000002">SO:1000002</a>
   */
  @JsonProperty("mnv")
  MNV,
  /**
   * A sequence alteration which included an insertion and a deletion, affecting two or more bases
   * and which is not a substitution.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:1000032">SO:1000032</a>
   * @see <a href="https://hgvs-nomenclature.org/recommendations/DNA/delins/">HGVS Nomenclature</a>
   */
  @JsonProperty("indel")
  INDEL,
  /**
   * Insertion of one or more bases.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0000667">SO:0000667</a>
   */
  @JsonProperty("insertion")
  INSERTION,
  /**
   * Deletion of one or more bases.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0000159">SO:0000159</a>
   */
  @JsonProperty("deletion")
  DELETION,
  /**
   * Structural alteration.
   *
   * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0001785">SO:0001785</a>
   */
  @JsonProperty("structural")
  STRUCTURAL,
  /**
   * Sequence variants that do not match other types such as 'missing value', 'allele missing due to
   * overlapping deletion' and 'the unspecified allele'.
   */
  @JsonProperty("other")
  OTHER
}
