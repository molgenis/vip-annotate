package org.molgenis.vipannotate.annotator.effect;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vipannotate.annotator.effect.model.Codon.from;

import java.util.Arrays;
import org.molgenis.vipannotate.annotator.effect.VariantEffect.VariantEffectBuilder;
import org.molgenis.vipannotate.annotator.effect.model.Codon;
import org.molgenis.vipannotate.annotator.effect.model.Consequence;
import org.molgenis.vcf.annotate.db.effect.model.*;
import org.molgenis.vipannotate.db.effect.model.FuryFactory;

public class SnpTranscriptEffectAnnotator {
  private final FuryFactory.AnnotationDb annotationDb;
  private final boolean annotateHgvs;

  public SnpTranscriptEffectAnnotator(FuryFactory.AnnotationDb annotationDb, boolean annotateHgvs) {
    this.annotationDb = requireNonNull(annotationDb);
    this.annotateHgvs = annotateHgvs;
  }

  /**
   * Annotate a sequence variant that changes the structure of the transcript.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001576">SO:0001576</a>
   */
  public VariantEffect annotateTranscriptVariant(
      int pos,
      byte[] ref,
      byte[] alt,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript) {
    VariantEffectBuilder variantEffectBuilder = VariantEffect.builder();

    FuryFactory.Exon[] exons = transcript.getExons();
    for (int i = 0; i < exons.length; i++) {
      FuryFactory.Exon exon = exons[i];

      if (exon.isOverlapping(pos, pos)) {
        annotateExonVariant(pos, ref, alt, strand, transcript, i, exon, variantEffectBuilder);
        break;
      } else if (isIntronVariant(pos, strand, exon)) {
        annotateIntronVariant(
            pos, ref, alt, strand, transcript, i, exons[i - 1], exon, variantEffectBuilder);
        break;
      }
    }

    return variantEffectBuilder.build();
  }

  /**
   * Annotate a sequence variant that changes exon sequence.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001791">SO:0001791</a>
   */
  private void annotateExonVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int exonIndex,
      FuryFactory.Exon exon,
      VariantEffectBuilder variantEffectBuilder) {

    FuryFactory.Cds cds = transcript.getCds();
    if (cds == null) {
      annotateNonCodingTranscriptExonVariant(
          pos, refBases, altBases, strand, transcript, exonIndex, exon, variantEffectBuilder);
    } else {
      if (isUtrVariant(pos, strand, cds)) {
        annotateUtrVariant(
            pos, refBases, altBases, strand, transcript, cds, exonIndex, variantEffectBuilder);
      } else {
        annotateCodingSequenceVariant(
            pos, refBases, altBases, strand, transcript, variantEffectBuilder);
      }
    }

    if (isSpliceRegionVariant(pos, exon)) {
      variantEffectBuilder.consequence(Consequence.SPLICE_REGION_VARIANT);
    }

    variantEffectBuilder.exonNumber(exonIndex + 1).exonTotal(transcript.getExons().length);
  }

  /**
   * A sequence variant that changes non-coding exon sequence in a non-coding transcript.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001792">SO:0001792</a>
   */
  private void annotateNonCodingTranscriptExonVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int exonIndex,
      FuryFactory.Exon exon,
      VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.NON_CODING_TRANSCRIPT_EXON_VARIANT);

    if (annotateHgvs) {
      variantEffectBuilder.hgvsC(
          SnpHgvsDescriber.calculateNonCodingTranscriptExonVariantHgvsC(
              pos, refBases, altBases, strand, transcript, exonIndex, exon));
    }
  }

  /**
   * A transcript variant that is located within the UTR.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001622">SO:0001622</a>
   */
  private void annotateUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      FuryFactory.Cds cds,
      int exonIndex,
      VariantEffectBuilder variantEffectBuilder) {
    boolean isFivePrimeUtrVariant = isFivePrimeUtrVariant(pos, strand, cds);
    if (isFivePrimeUtrVariant) {
      annotateFivePrimeUtrVariant(
          pos, refBases, altBases, strand, transcript, exonIndex, variantEffectBuilder);
    } else {
      annotateThreePrimeUtrVariant(
          pos, refBases, altBases, strand, transcript, exonIndex, variantEffectBuilder);
    }
  }

  /**
   * Annotate a UTR variant of the 5' UTR.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001623">SO:0001623</a>
   */
  private void annotateFivePrimeUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int exonIndex,
      VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.FIVE_PRIME_UTR_VARIANT);

    if (annotateHgvs) {
      variantEffectBuilder.hgvsC(
          SnpHgvsDescriber.calculateHgvsCFivePrimeUtrVariant(
              pos, refBases, altBases, strand, transcript, exonIndex));
    }
  }

  /**
   * Annotate a UTR variant of the 3' UTR.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001624">SO:0001624</a>
   */
  private void annotateThreePrimeUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int exonIndex,
      VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.THREE_PRIME_UTR_VARIANT);

    if (annotateHgvs) {
      variantEffectBuilder.hgvsC(
          SnpHgvsDescriber.calculateHgvsCThreePrimeUtrVariant(
              pos, refBases, altBases, strand, transcript, exonIndex));
    }
  }

  /**
   * Annotate a sequence variant that changes the coding sequence.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001580">SO:0001580</a>
   */
  private void annotateCodingSequenceVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      VariantEffectBuilder variantEffectBuilder) {
    FuryFactory.Cds cds = transcript.getCds();
    int cdsFragmentId = cds.findAnyFragmentId(pos, pos);
    FuryFactory.Cds.Fragment cdsFragment = cds.fragments()[cdsFragmentId];
    int codonPos = getCodonPos(pos, strand, cdsFragment);

    CodonVariant codonVariant =
        getReferenceSequenceCodon(pos, altBases, strand, cds, cdsFragmentId, codonPos);

    if (isInitiatorCodonVariant(pos, strand, cds)) {
      annotateInitiatorCodonVariant(variantEffectBuilder);
    } else if (isTerminatorCodonVariant(pos, strand, cds)) {
      annotateTerminatorCodonVariant(codonVariant, variantEffectBuilder);
    } else if (isProteinAlteringVariant(codonVariant)) {
      annotateProteinAlteringVariant(codonVariant, variantEffectBuilder);
    } else {
      annotateSynonymousVariant(variantEffectBuilder);
    }

    if (annotateHgvs) {
      SnpHgvsDescriber.Hgvs hgvs =
          SnpHgvsDescriber.calculateHgvsCodingSequenceVariant(
              pos, refBases, altBases, strand, transcript, cdsFragmentId, codonVariant, codonPos);

      variantEffectBuilder.hgvsC(hgvs.hgvsC()).hgvsP(hgvs.hgvsP());
    }
  }

  /**
   * Annotate a codon variant that changes at least one base of the first codon of a transcript.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001582">SO:0001582</a>
   */
  private void annotateInitiatorCodonVariant(VariantEffectBuilder variantEffectBuilder) {
    // TODO support https://en.wikipedia.org/wiki/Start_codon#Alternative_start_codons
    // start codon must be 'ATG' so any variant implies a start lost
    variantEffectBuilder.consequence(Consequence.START_LOST);
  }

  /**
   * Annotate a sequence variant whereby at least one of the alt in the terminator codon is changed.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001590">SO:0001590</a>
   */
  private void annotateTerminatorCodonVariant(
      CodonVariant codonVariant, VariantEffectBuilder variantEffectBuilder) {
    Consequence consequence =
        codonVariant.alt().isStopCodon()
            ? Consequence.STOP_RETAINED_VARIANT
            : Consequence.STOP_LOST;
    variantEffectBuilder.consequence(consequence);
  }

  /**
   * Annotate a sequence_variant which is predicted to change the protein encoded in the coding
   * sequence.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001818">SO:0001818</a>
   */
  private void annotateProteinAlteringVariant(
      CodonVariant codonVariant, VariantEffectBuilder variantEffectBuilder) {
    Consequence consequence =
        codonVariant.alt().isStopCodon() ? Consequence.STOP_GAINED : Consequence.MISSENSE_VARIANT;
    variantEffectBuilder.consequence(consequence);
  }

  /**
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0001819">SO:0001819</a>
   */
  private void annotateSynonymousVariant(VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.SYNONYMOUS_VARIANT);
  }

  /**
   * Annotate a transcript variant occurring within an intron.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001627">SO:0001627</a>
   */
  private void annotateIntronVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int threePrimeExonIndex,
      FuryFactory.Exon fivePrimeExon,
      FuryFactory.Exon threePrimeExon,
      VariantEffectBuilder variantEffectBuilder) {
    if (isSpliceAcceptorVariant(pos, strand, threePrimeExon)) {
      variantEffectBuilder.consequence(Consequence.SPLICE_ACCEPTOR_VARIANT);
    } else if (isSpliceDonorVariant(pos, strand, fivePrimeExon)) {
      variantEffectBuilder.consequence(Consequence.SPLICE_DONOR_VARIANT);
    } else {
      if (isSpliceDonor5thBaseVariant(pos, strand, fivePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_DONOR_5TH_BASE_VARIANT);
      } else if (isSpliceDonorRegionVariant(pos, strand, fivePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_DONOR_REGION_VARIANT);
      } else if (isSpliceRegionVariant(pos, strand, threePrimeExon, fivePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_REGION_VARIANT);
      }
      if (isSplicePolypyrimidineTractVariant(pos, strand, threePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_POLYPYRIMIDINE_TRACT_VARIANT);
      }
      variantEffectBuilder.consequence(Consequence.INTRON_VARIANT);
    }
    if (transcript.getCds() == null) {
      variantEffectBuilder.consequence(Consequence.NON_CODING_TRANSCRIPT_VARIANT);
    }
    variantEffectBuilder
        .intronNumber(threePrimeExonIndex)
        .intronTotal(transcript.getExons().length - 1);

    if (annotateHgvs) {
      String hgvsC =
          SnpHgvsDescriber.calculateHgvsCIntronVariant(
              pos,
              refBases,
              altBases,
              strand,
              transcript,
              fivePrimeExon,
              threePrimeExonIndex,
              threePrimeExon);
      variantEffectBuilder.hgvsC(hgvsC);
    }
  }

  /**
   * A splice variant that changes the 2 base region at the 3' end of an intron.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001574">SO:0001574</a>
   */
  private static boolean isSpliceAcceptorVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Exon exon) {
    return switch (strand) {
      case POSITIVE -> exon.getStart() - pos == 1 || exon.getStart() - pos == 2;
      case NEGATIVE -> pos - exon.getStop() == 1 || pos - exon.getStop() == 2;
    };
  }

  /**
   * A splice variant that changes the 2 base pair region at the 5' end of an intron.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001575">SO:0001575</a>
   */
  private static boolean isSpliceDonorVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Exon exon) {
    return switch (strand) {
      case POSITIVE -> pos - exon.getStop() == 1 || pos - exon.getStop() == 2;
      case NEGATIVE -> exon.getStart() - pos == 1 || exon.getStart() - pos == 2;
    };
  }

  private static boolean isIntronVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Exon nextAdjacentExon) {
    return switch (strand) {
      case POSITIVE -> pos < nextAdjacentExon.getStart();
      case NEGATIVE -> pos > nextAdjacentExon.getStop();
    };
  }

  private static boolean isSpliceDonor5thBaseVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Exon fivePrimeExon) {
    return switch (strand) {
      case POSITIVE -> pos - fivePrimeExon.getStop() == 5;
      case NEGATIVE -> fivePrimeExon.getStart() - pos == 5;
    };
  }

  private static boolean isSplicePolypyrimidineTractVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Exon threePrimeExon) {
    return switch (strand) {
      case POSITIVE ->
          threePrimeExon.getStart() - pos >= 3 && threePrimeExon.getStart() - pos <= 17;
      case NEGATIVE -> pos - threePrimeExon.getStop() >= 3 && pos - threePrimeExon.getStop() <= 17;
    };
  }

  private static boolean isSpliceDonorRegionVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Exon fivePrimeExon) {
    return switch (strand) {
      case POSITIVE -> pos - fivePrimeExon.getStop() >= 3 && pos - fivePrimeExon.getStop() <= 6;
      case NEGATIVE -> fivePrimeExon.getStart() - pos >= 3 && fivePrimeExon.getStart() - pos <= 6;
    };
  }

  private static boolean isSpliceRegionVariant(int pos, FuryFactory.Exon exon) {
    return (pos - exon.getStart() + 1 >= 1 && pos - exon.getStart() + 1 <= 3)
        || (exon.getStop() - pos + 1 >= 1 && exon.getStop() - pos + 1 <= 3);
  }

  private boolean isSpliceRegionVariant(
      int pos,
      FuryFactory.Strand strand,
      FuryFactory.Exon threePrimeExon,
      FuryFactory.Exon fivePrimeExon) {
    return switch (strand) {
      case POSITIVE ->
          (pos - fivePrimeExon.getStop() >= 3 && pos - fivePrimeExon.getStop() <= 8)
              || (threePrimeExon.getStart() - pos >= 3 && threePrimeExon.getStart() - pos <= 8);
      case NEGATIVE ->
          (fivePrimeExon.getStart() - pos >= 3 && fivePrimeExon.getStart() - pos <= 8)
              || (pos - threePrimeExon.getStop() >= 3 && pos - threePrimeExon.getStop() <= 8);
    };
  }

  private static boolean isUtrVariant(int pos, FuryFactory.Strand strand, FuryFactory.Cds cds) {
    FuryFactory.Cds.Fragment[] fragments = cds.fragments();
    return switch (strand) {
      case POSITIVE ->
          pos < fragments[0].getStart() || pos > fragments[fragments.length - 1].getStop();
      case NEGATIVE ->
          pos > fragments[0].getStop() || pos < fragments[fragments.length - 1].getStart();
    };
  }

  private static boolean isTerminatorCodonVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Cds cds) {
    FuryFactory.Cds.Fragment cdsFragment = cds.fragments()[cds.fragments().length - 1];
    return switch (strand) {
      case POSITIVE -> cdsFragment.getStop() - pos < Codon.NR_NUCLEOTIDES;
      case NEGATIVE -> pos - cdsFragment.getStart() < Codon.NR_NUCLEOTIDES;
    };
  }

  private static boolean isInitiatorCodonVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Cds cds) {
    FuryFactory.Cds.Fragment cdsFragment = cds.fragments()[0];
    return switch (strand) {
      case POSITIVE -> pos - cdsFragment.getStart() - cdsFragment.getPhase() < Codon.NR_NUCLEOTIDES;
      case NEGATIVE -> cdsFragment.getStop() - pos - cdsFragment.getPhase() < Codon.NR_NUCLEOTIDES;
    };
  }

  private static boolean isFivePrimeUtrVariant(
      int pos, FuryFactory.Strand strand, FuryFactory.Cds cds) {
    return switch (strand) {
      case POSITIVE -> pos < cds.fragments()[0].getStart();
      case NEGATIVE -> pos > cds.fragments()[0].getStop();
    };
  }

  private static boolean isProteinAlteringVariant(CodonVariant codonVariant) {
    return codonVariant.ref().getAminoAcid() != codonVariant.alt().getAminoAcid();
  }

  /**
   * @return relative position 0, 1 or 2 in the codon
   */
  private static int getCodonPos(int pos, FuryFactory.Strand strand, FuryFactory.Cds.Fragment cds) {
    return switch (strand) {
      case FuryFactory.Strand.POSITIVE -> (3 + (pos - cds.getStart() - cds.getPhase())) % 3;
      case FuryFactory.Strand.NEGATIVE -> (3 + (cds.getStop() - pos - cds.getPhase())) % 3;
    };
  }

  private CodonVariant getReferenceSequenceCodon(
      int pos,
      byte[] alt,
      FuryFactory.Strand strand,
      FuryFactory.Cds cds,
      int cdsFragmentId,
      int codonPos) {
    FuryFactory.Cds.Fragment cdsFragment = cds.fragments()[cdsFragmentId];
    char[] refSequence =
        switch (strand) {
          case FuryFactory.Strand.POSITIVE -> {
            // codon can be spliced
            if (pos - codonPos < cdsFragment.getStart()) {
              FuryFactory.Cds.Fragment otherCdsFragment = cds.fragments()[cdsFragmentId - 1];
              char[] second =
                  annotationDb.getSequence(cdsFragment.getStart(), pos + (2 - codonPos), strand);
              char[] first =
                  annotationDb.getSequence(
                      otherCdsFragment.getStop() - (2 - second.length),
                      otherCdsFragment.getStop(),
                      strand);
              char[] both = Arrays.copyOf(first, first.length + second.length);
              System.arraycopy(second, 0, both, first.length, second.length);
              yield both;
            } else if (pos - codonPos + 2 > cdsFragment.getStop()) {
              FuryFactory.Cds.Fragment otherCdsFragment = cds.fragments()[cdsFragmentId + 1];
              char[] first =
                  annotationDb.getSequence(pos - codonPos, cdsFragment.getStop(), strand);
              char[] second =
                  annotationDb.getSequence(
                      otherCdsFragment.getStart(),
                      otherCdsFragment.getStart() + (2 - first.length),
                      strand);
              char[] both = Arrays.copyOf(first, first.length + second.length);
              System.arraycopy(second, 0, both, first.length, second.length);
              yield both;
            } else {
              yield annotationDb.getSequence(pos - codonPos, pos - codonPos + 2, strand);
            }
          }
          case FuryFactory.Strand.NEGATIVE -> {
            if (pos + codonPos > cdsFragment.getStop()) {
              FuryFactory.Cds.Fragment otherCdsFragment = cds.fragments()[cdsFragmentId - 1];
              char[] second =
                  annotationDb.getSequence(cdsFragment.getStop(), pos - (2 - codonPos), strand);
              char[] first =
                  annotationDb.getSequence(
                      otherCdsFragment.getStart() + (2 - second.length),
                      otherCdsFragment.getStart(),
                      strand);

              char[] both = Arrays.copyOf(first, first.length + second.length);
              System.arraycopy(second, 0, both, first.length, second.length);
              yield both;
            } else if (pos + codonPos - 2 < cdsFragment.getStart()) {
              FuryFactory.Cds.Fragment otherCdsFragment = cds.fragments()[cdsFragmentId + 1];
              char[] first =
                  annotationDb.getSequence(pos + codonPos, cdsFragment.getStart(), strand);
              char[] second =
                  annotationDb.getSequence(
                      otherCdsFragment.getStop(),
                      otherCdsFragment.getStop() - (2 - first.length),
                      strand);

              char[] both = Arrays.copyOf(first, first.length + second.length);
              System.arraycopy(second, 0, both, first.length, second.length);
              yield both;
            } else {
              yield annotationDb.getSequence(pos + codonPos, pos + codonPos - 2, strand);
            }
          }
        };

    char[] altSequence = new char[3];
    System.arraycopy(refSequence, 0, altSequence, 0, 3);

    altSequence[codonPos] =
        switch (strand) {
          case POSITIVE -> (char) alt[0];
          case NEGATIVE -> (char) SequenceUtils.getComplementaryBase(alt[0]);
        };

    return new CodonVariant(from(refSequence), from(altSequence));
  }
}
