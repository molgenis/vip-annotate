package org.molgenis.vcf.annotate.annotator.effect.model;

import static java.util.Objects.requireNonNull;

import lombok.Getter;

@Getter
public enum Consequence {
  /**
   * A splice variant that changes the 2 base region at the 3' end of an intron.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001574">SO:0001574</a>
   */
  SPLICE_ACCEPTOR_VARIANT("splice_acceptor_variant", Impact.HIGH),
  /**
   * A splice variant that changes the 2 base pair region at the 5' end of an intron.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001575">SO:0001575</a>
   */
  SPLICE_DONOR_VARIANT("splice_donor_variant", Impact.HIGH),
  /**
   * A sequence variant whereby at least one base of a codon is changed, resulting in a premature
   * stop codon, leading to a shortened transcript.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001587">SO:0001587</a>
   */
  STOP_GAINED("stop_gained", Impact.HIGH),
  /**
   * A sequence variant where at least one base of the terminator codon (stop) is changed, resulting
   * in an elongated transcript.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001578">SO:0001578</a>
   */
  STOP_LOST("stop_lost", Impact.HIGH),
  /**
   * A codon variant that changes at least one base of the canonical start codon.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0002012">SO:0002012</a>
   */
  START_LOST("start_lost", Impact.HIGH),
  /**
   * A sequence variant, that changes one or more alt, resulting in a different amino acid sequence
   * but where the length is preserved.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001583">SO:0001583</a>
   */
  MISSENSE_VARIANT("missense_variant", Impact.MODERATE),
  /**
   * A sequence variant that causes a change at the 5th base pair after the start of the intron in
   * the orientation of the transcript.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001787">SO:0001787</a>
   */
  SPLICE_DONOR_5TH_BASE_VARIANT("splice_donor_5th_base_variant", Impact.LOW),
  /**
   * A sequence variant in which a change has occurred within the region of the splice site, either
   * within 1-3 alt of the exon or 3-8 alt of the intron.
   *
   * @see <a
   *     href=http://sequenceontology.org/browser/current_release/term/SO:0001630">SO:0001630</a>
   */
  SPLICE_REGION_VARIANT("splice_region_variant", Impact.LOW),
  /**
   * A sequence variant that falls in the region between the 3rd and 6th base after splice junction
   * (5' end of intron).
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0002170">SO:0002170</a>
   */
  SPLICE_DONOR_REGION_VARIANT("splice_donor_region_variant", Impact.LOW),
  /**
   * A sequence variant that falls in the polypyrimidine tract at 3' end of intron between 17 and 3
   * alt from the end (acceptor -3 to acceptor -17).
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0002169">SO:0002169</a>
   */
  SPLICE_POLYPYRIMIDINE_TRACT_VARIANT("splice_polypyrimidine_tract_variant", Impact.LOW),
  /**
   * A sequence variant where at least one base in the start codon is changed, but the start
   * remains.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0002019">SO:0002019</a>
   */
  START_RETAINED_VARIANT("start_retained_variant", Impact.LOW),
  /**
   * A sequence variant where at least one base in the terminator codon is changed, but the
   * terminator remains.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001567">SO:0001567</a>
   */
  STOP_RETAINED_VARIANT("stop_retained_variant", Impact.LOW),
  /**
   * A sequence variant where there is no resulting change to the encoded amino acid.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001819">SO:0001819</a>
   */
  SYNONYMOUS_VARIANT("synonymous_variant", Impact.LOW),
  /**
   * A UTR variant of the 5' UTR.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001623">SO:0001623</a>
   */
  FIVE_PRIME_UTR_VARIANT("5_prime_UTR_variant", Impact.MODIFIER),
  /**
   * A UTR variant of the 3' UTR.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001624">SO:0001624</a>
   */
  THREE_PRIME_UTR_VARIANT("3_prime_UTR_variant", Impact.MODIFIER),
  /**
   * A sequence variant that changes non-coding exon sequence in a non-coding transcript.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001792">SO:0001792</a>
   */
  NON_CODING_TRANSCRIPT_EXON_VARIANT("non_coding_transcript_exon_variant", Impact.MODIFIER),
  /**
   * A transcript variant occurring within an intron.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001627">SO:0001627</a>
   */
  INTRON_VARIANT("intron_variant", Impact.MODIFIER),
  /**
   * A transcript variant of a non coding RNA gene.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001619">SO:0001619</a>
   */
  NON_CODING_TRANSCRIPT_VARIANT("non_coding_transcript_variant", Impact.MODIFIER),
  /**
   * A sequence variant located in the intergenic region, between genes.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001628">SO:0001628</a>
   */
  INTERGENIC_VARIANT("intergenic_variant", Impact.MODIFIER);

  private final String term;
  private final Impact impact;

  Consequence(String term, Impact impact) {
    this.term = requireNonNull(term);
    this.impact = requireNonNull(impact);
  }
}
