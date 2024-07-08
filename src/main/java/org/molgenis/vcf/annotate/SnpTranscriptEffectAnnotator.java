package org.molgenis.vcf.annotate;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.annotate.model.Codon.from;

import java.util.Arrays;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.model.*;

public class SnpTranscriptEffectAnnotator {
  private final AnnotationDb annotationDb;

  public SnpTranscriptEffectAnnotator(AnnotationDb annotationDb) {
    this.annotationDb = requireNonNull(annotationDb);
  }

  /**
   * Annotate a sequence variant that changes the structure of the transcript.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001576">SO:0001576</a>
   */
  public VariantEffect annotateTranscriptVariant(
      int pos, byte[] ref, byte[] alt, Strand strand, Transcript transcript) {
    VariantEffect.VariantEffectBuilder variantEffectBuilder = VariantEffect.builder();
    Exon[] exons = transcript.getExons();

    // TODO performance: replace with binary search
    int nrExons = exons.length;
    for (int i = 0; i < nrExons; i++) {
      Exon exon = exons[i];

      if (exon.isOverlapping(pos, pos)) {
        // exon
        String exonPos = getExonNr(i) + "/" + nrExons;
        variantEffectBuilder.exon(exonPos);
        annotateExonVariant(pos, ref, alt, strand, transcript, i, exon, variantEffectBuilder);
        break;
      } else if (isOverlappingIntron(pos, strand, exon)) {
        // intron
        String intronPos = (getExonNr(i) - 1) + "/" + (nrExons - 1);
        variantEffectBuilder.intron(intronPos);
        annotateIntronVariant(
            pos, ref, alt, strand, transcript, i, exons[i - 1], exon, variantEffectBuilder);
        break;
      }
    }

    return variantEffectBuilder.build();
  }

  /**
   * A splice variant that changes the 2 base region at the 3' end of an intron.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001574">SO:0001574</a>
   */
  private boolean isSpliceAcceptorVariant(int pos, Strand strand, Exon exon) {
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
  private boolean isSpliceDonorVariant(int pos, Strand strand, Exon exon) {
    return switch (strand) {
      case POSITIVE -> pos - exon.getStop() == 1 || pos - exon.getStop() == 2;
      case NEGATIVE -> exon.getStart() - pos == 1 || exon.getStart() - pos == 2;
    };
  }

  private int getExonNr(int exonIndex) {
    return exonIndex + 1;
  }

  private boolean isOverlappingIntron(int pos, Strand strand, Exon nextAdjacentExon) {
    return switch (strand) {
      case POSITIVE -> pos < nextAdjacentExon.getStart();
      case NEGATIVE -> pos > nextAdjacentExon.getStop();
    };
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
      Strand strand,
      Transcript transcript,
      int threePrimeExonIndex,
      Exon fivePrimeExon,
      Exon threePrimeExon,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    if (isSpliceAcceptorVariant(pos, strand, threePrimeExon)) {
      variantEffectBuilder.consequence(Consequence.SPLICE_ACCEPTOR_VARIANT);
    } else if (isSpliceDonorVariant(pos, strand, fivePrimeExon)) {
      variantEffectBuilder.consequence(Consequence.SPLICE_DONOR_VARIANT);
    } else {
      if (isSpliceDonor5thBaseVariant(pos, strand, fivePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_DONOR_5TH_BASE_VARIANT);
      } else if (isSpliceDonorRegionVariant(pos, strand, fivePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_DONOR_REGION_VARIANT);
      } else if (isSplicePolypyrimidineTractVariant(pos, strand, threePrimeExon)) {
        variantEffectBuilder.consequence(Consequence.SPLICE_POLYPYRIMIDINE_TRACT_VARIANT);
      }
      // TODO use non_coding_transcript_intron_variant
      variantEffectBuilder.consequence(Consequence.INTRON_VARIANT);
    }
    if (transcript.getCds() == null) {
      variantEffectBuilder.consequence(Consequence.NON_CODING_TRANSCRIPT_VARIANT);
    }

    String hgvsC;
    Cds cds = transcript.getCds();
    if (cds != null) {
      hgvsC =
          calculateIntronVariantCodingDnaHgvsC(
              pos, refBases, altBases, strand, transcript, fivePrimeExon, threePrimeExon);
    } else {
      hgvsC =
          calculateIntronVariantNonCodingDnaHgvsC(
              pos,
              refBases,
              altBases,
              strand,
              transcript,
              threePrimeExonIndex,
              fivePrimeExon,
              threePrimeExon);
    }
    variantEffectBuilder.hgvsC(hgvsC);
  }

  private boolean isSpliceDonor5thBaseVariant(int pos, Strand strand, Exon fivePrimeExon) {
    return switch (strand) {
      case POSITIVE -> pos - fivePrimeExon.getStop() == 5;
      case NEGATIVE -> fivePrimeExon.getStart() - pos == 5;
    };
  }

  private boolean isSplicePolypyrimidineTractVariant(int pos, Strand strand, Exon threePrimeExon) {
    return switch (strand) {
      case POSITIVE ->
          threePrimeExon.getStart() - pos >= 3 && threePrimeExon.getStart() - pos <= 17;
      case NEGATIVE -> pos - threePrimeExon.getStop() >= 3 && pos - threePrimeExon.getStop() <= 17;
    };
  }

  private boolean isSpliceDonorRegionVariant(int pos, Strand strand, Exon fivePrimeExon) {
    return switch (strand) {
      case POSITIVE -> pos - fivePrimeExon.getStop() >= 3 && pos - fivePrimeExon.getStop() <= 6;
      case NEGATIVE -> fivePrimeExon.getStart() - pos >= 3 && fivePrimeExon.getStart() - pos <= 6;
    };
  }

  /**
   * coding DNA reference sequences: introns
   *
   * <ul>
   *   <li>nucleotides at the 5' end of an intron are numbered relative to the last nucleotide of
   *       the directly upstream exon, followed by a + (plus) and their position in the intron, like
   *       c.87+1, c.87+2, c.87+3, ..., etc.
   *   <li>nucleotides at the 3' end of an intron are numbered relative to the first nucleotide of
   *       the directly downstream exon, followed by a - (hyphen-minus) and their position away from
   *       that exon, like ..., c.88-3, c.88-2, c.88-1.
   *       <ul>
   *         <li>in the middle of the intron, nucleotide numbering changes from + (plus) to -
   *             (hyphen-minus); e.g., ..., c.87+676, c.87+677, c.87+678, c.88-678, c.88-677,
   *             c.88-676, ...
   *         <li>in introns with an uneven number of nucleotides, the central nucleotide is numbered
   *             relative to the upstream exon followed by a + (plus): e.g., ..., c.87+676,
   *             c.87+677, c.87+678, c.87+679, c.88-678, c.88-677, c.88-676, ...
   *       </ul>
   * </ul>
   *
   * @see <a
   *     href="https://hgvs-nomenclature.org/stable/background/numbering/#coding-dna-reference-sequences">HGVS
   *     Nomenclature v21.0.2</a>
   */
  private String calculateIntronVariantCodingDnaHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      Exon fivePrimeExon,
      Exon threePrimeExon) {

    int exonPos;
    int intronPos;
    switch (strand) {
      case POSITIVE -> {
        int intronCenter =
            fivePrimeExon.getStop() + ((threePrimeExon.getStart() - fivePrimeExon.getStop()) / 2);
        exonPos = pos <= intronCenter ? fivePrimeExon.getStop() : threePrimeExon.getStart();
        intronPos = pos - exonPos;
      }
      case NEGATIVE -> {
        int intronCenter =
            threePrimeExon.getStop() + ((fivePrimeExon.getStart() - threePrimeExon.getStop()) / 2);
        exonPos = pos <= intronCenter ? threePrimeExon.getStop() : fivePrimeExon.getStart();
        intronPos = exonPos - pos;
      }
      default -> throw new IllegalStateException("Unexpected value: " + strand);
    }

    Cds.Fragment cdsFragment = transcript.getCds().findAnyFragment(exonPos, exonPos);
    int codingReferenceSequencePos = getCdsPos(exonPos, strand, transcript, cdsFragment);

    char ref = getBase(refBases, strand);
    char alt = getBase(altBases, strand);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(transcript.getId()).append(":c.").append(codingReferenceSequencePos);
    // FIXME c.* if intron in 3'UTR
    if (intronPos >= 0) {
      stringBuilder.append('+');
    }
    stringBuilder.append(intronPos).append(ref).append('>').append(alt);
    return stringBuilder.toString();
  }

  /**
   * non-coding DNA reference sequences: introns
   *
   * <ul>
   *   <li>nucleotide numbering is n.1, n.2, n.3, ..., etc., from the first to the last nucleotide
   *       of the reference sequence.
   *   <li>nucleotides in introns are numbered as for coding DNA reference sequences (see above),
   *       although proceeded by n. (not c.).
   *   <li>it is not allowed to describe variants in nucleotides which are not covered by the
   *       transcript, using only a non-coding DNA reference sequence.
   * </ul>
   *
   * @see <a
   *     href="https://hgvs-nomenclature.org/stable/background/numbering/#non-coding-dna-reference-sequences">HGVS
   *     Nomenclature v21.0.2</a>
   */
  private String calculateIntronVariantNonCodingDnaHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      int threePrimeExonIndex,
      Exon fivePrimeExon,
      Exon threePrimeExon) {
    int intronPos;
    int transcriptPos = 0;
    switch (strand) {
      case POSITIVE -> {
        int intronCenter =
            fivePrimeExon.getStop() + ((threePrimeExon.getStart() - fivePrimeExon.getStop()) / 2);
        int exonPos;
        if (pos <= intronCenter) {
          exonPos = fivePrimeExon.getStop();

          for (Exon exon : transcript.getExons()) {
            if (exon.isOverlapping(exonPos, exonPos)) {
              transcriptPos += exon.getLength();
              break;
            } else {
              transcriptPos += exon.getLength();
            }
          }
        } else {
          exonPos = threePrimeExon.getStart();

          for (Exon exon : transcript.getExons()) {
            if (exon.isOverlapping(exonPos, exonPos)) {
              transcriptPos += 1;
              break;
            } else {
              transcriptPos += exon.getLength();
            }
          }
        }
        intronPos = pos - exonPos;
      }
      case NEGATIVE -> {
        int intronCenter =
            threePrimeExon.getStop() + ((fivePrimeExon.getStart() - threePrimeExon.getStop()) / 2);
        int exonPos;
        if (pos <= intronCenter) {
          exonPos = threePrimeExon.getStop();
          for (Exon exon : transcript.getExons()) {
            if (exon.isOverlapping(exonPos, exonPos)) {
              transcriptPos += 1;
              break;
            } else {
              transcriptPos += exon.getLength();
            }
          }
        } else {
          exonPos = fivePrimeExon.getStart();
          for (Exon exon : transcript.getExons()) {
            if (exon.isOverlapping(exonPos, exonPos)) {
              transcriptPos += exon.getLength();
              break;
            } else {
              transcriptPos += exon.getLength();
            }
          }
        }
        intronPos = exonPos - pos;
      }
      default -> throw new IllegalStateException("Unexpected value: " + strand);
    }

    char ref = getBase(refBases, strand);
    char alt = getBase(altBases, strand);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(transcript.getId()).append(":n.").append(transcriptPos);
    if (intronPos >= 0) {
      stringBuilder.append('+');
    }
    stringBuilder.append(intronPos).append(ref).append('>').append(alt);
    return stringBuilder.toString();
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
      Strand strand,
      Transcript transcript,
      int exonIndex,
      Exon exon,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {

    Cds cds = transcript.getCds();
    if (cds == null) {
      annotateNonCodingTranscriptExonVariant(
          pos, refBases, altBases, strand, transcript, exonIndex, exon, variantEffectBuilder);
    } else {
      Cds.Fragment[] fragments = cds.fragments();
      if (isUtrVariant(pos, strand, fragments)) {
        annotateUtrVariant(
            pos,
            refBases,
            altBases,
            strand,
            transcript,
            cds,
            exonIndex,
            exon,
            variantEffectBuilder);
      } else {
        annotateCodingSequenceVariant(
            pos, refBases, altBases, strand, transcript, cds, variantEffectBuilder);
      }
    }

    if ((pos - exon.getStart() >= 0 && pos - exon.getStart() <= 2)
        || (exon.getStop() - pos >= 0 && exon.getStop() - pos <= 2)) {
      variantEffectBuilder.consequence(Consequence.SPLICE_REGION_VARIANT);
    }
  }

  private static boolean isUtrVariant(int pos, Strand strand, Cds.Fragment[] fragments) {
    return switch (strand) {
      case POSITIVE ->
          pos < fragments[0].getStart() || pos > fragments[fragments.length - 1].getStop();
      case NEGATIVE ->
          pos > fragments[0].getStop() || pos < fragments[fragments.length - 1].getStart();
    };
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
      Strand strand,
      Transcript transcript,
      int exonIndex,
      Exon exon,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.NON_CODING_TRANSCRIPT_EXON_VARIANT);
    String hgvsC =
        calculateNonCodingTranscriptExonVariantHgvsC(
            pos, refBases, altBases, strand, transcript, exonIndex, exon);
    variantEffectBuilder.hgvsC(hgvsC);
  }

  /**
   * non-coding DNA reference sequences: *
   *
   * <ul>
   *   <li>nucleotide numbering is n.1, n.2, n.3, ..., etc., from the first to the last nucleotide
   *       of the reference sequence.
   *   <li>nucleotides in introns are numbered as for coding DNA reference sequences (see above),
   *       although proceeded by n. (not c.).
   *   <li>it is not allowed to describe variants in nucleotides which are not covered by the
   *       transcript, using only a non-coding DNA reference sequence.
   * </ul>
   *
   * @see <a
   *     href="https://hgvs-nomenclature.org/stable/background/numbering/#non-coding-dna-reference-sequences">HGVS
   *     Nomenclature v21.0.2</a>
   */
  private String calculateNonCodingTranscriptExonVariantHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      int exonIndex,
      Exon exon) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(transcript.getId()).append(":n.");
    int nPos =
        switch (strand) {
          case POSITIVE -> pos - exon.getStart() + 1;
          case NEGATIVE -> exon.getStop() - pos + 1;
        };
    for (int i = 0; i < exonIndex; ++i) {
      nPos += transcript.getExons()[i].getLength();
    }
    stringBuilder.append(nPos);
    char ref = getBase(refBases, strand);
    char alt = getBase(altBases, strand);
    stringBuilder.append(ref).append('>').append(alt);
    return stringBuilder.toString();
  }

  private static byte getComplementaryBase(byte refBase) {
    return switch (refBase) {
      case 'A' -> 'T';
      case 'C' -> 'G';
      case 'G' -> 'C';
      case 'T' -> 'A';
      case 'N' -> 'N';
      default -> throw new IllegalArgumentException();
    };
  }

  private int getCdsPos(int pos, Strand strand, Transcript transcript, Cds.Fragment cdsFragment) {
    int codingReferenceSequencePos;
    if (cdsFragment != null) {
      codingReferenceSequencePos = 0;
      for (Cds.Fragment fragment : transcript.getCds().fragments()) {
        cdsFragment = fragment;

        if (strand == Strand.POSITIVE) {
          if (pos > cdsFragment.getStop()) {
            codingReferenceSequencePos += cdsFragment.getLength();
          } else {
            codingReferenceSequencePos += pos - cdsFragment.getStart() + 1;
            break;
          }
        } else if (strand == Strand.NEGATIVE) {
          if (pos < cdsFragment.getStart()) {
            codingReferenceSequencePos += cdsFragment.getLength();
          } else {
            codingReferenceSequencePos += cdsFragment.getStop() - pos + 1;
            break;
          }
        } else throw new RuntimeException();
      }
    } else {
      // find overlapping exon
      Exon[] exons = transcript.getExons();
      int exonIndex;
      for (exonIndex = 0; exonIndex < exons.length; exonIndex++) {
        if (exons[exonIndex].isOverlapping(pos, pos)) break;
      }

      cdsFragment = transcript.getCds().fragments()[0];
      switch (strand) {
        case POSITIVE -> {
          if (pos < cdsFragment.getStart()) {
            codingReferenceSequencePos = 0;
            Exon exon = exons[exonIndex];
            while (!exon.isOverlapping(cdsFragment.getStart(), cdsFragment.getStop())) {
              if (exon.isOverlapping(pos, pos)) {
                codingReferenceSequencePos -= exon.getStop() - pos + 1;
              } else {
                codingReferenceSequencePos -= exon.getLength();
              }
              exon = transcript.getExons()[++exonIndex];
            }
            codingReferenceSequencePos -= cdsFragment.getStart() - exon.getStart();
          } else {
            // FIXME implement
            return 666;
          }
        }
        case NEGATIVE -> {
          if (pos > cdsFragment.getStop()) {
            codingReferenceSequencePos = 0;
            Exon exon = exons[exonIndex];
            while (!exon.isOverlapping(cdsFragment.getStart(), cdsFragment.getStop())) {
              if (exon.isOverlapping(pos, pos)) {
                codingReferenceSequencePos -= pos - exon.getStart() + 1;
              } else {
                codingReferenceSequencePos -= exon.getLength();
              }
              exon = transcript.getExons()[++exonIndex];
            }
            codingReferenceSequencePos -= exon.getStop() - cdsFragment.getStop();
          } else {
            // FIXME implement
            return 666;
          }
        }
        default -> throw new IllegalArgumentException();
      }
    }

    return codingReferenceSequencePos;
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
      Strand strand,
      Transcript transcript,
      Cds cds,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {

    Cds.Fragment cdsFragment = transcript.getCds().findAnyFragment(pos, pos);
    int codingPos = getCdsPos(pos, strand, transcript, cdsFragment);

    if (isStartCodonVariant(pos, strand, cds)) {
      annotateCodingSequenceStartCodonVariant(
          pos, strand, transcript, codingPos, cdsFragment, variantEffectBuilder);

    } else if (isStopCodonVariant(pos, strand, cds)) {
      CodonVariant codonVariant =
          getReferenceSequenceCodon(pos, altBases, strand, cds, cdsFragment);

      annotateCodingSequenceStopCodonVariant(pos, codonVariant, variantEffectBuilder);

      // TODO deduplicate
      // calc hgvs

      int codonPos = getCodonPos(pos, strand, cdsFragment);
      String hgvsP =
          cds.proteinId()
              + ":p."
              + (codonVariant.ref().isStopCodon()
                  ? "Ter"
                  : codonVariant.ref().getAminoAcid().getTerm())
              + (((codingPos - codonPos) / 3) + 1)
              + (codonVariant.alt().isStopCodon()
                  ? "Ter"
                  : codonVariant.alt().getAminoAcid().getTerm());
      variantEffectBuilder.hgvsP(hgvsP);
    } else {
      annotateCodingSequenceCodonVariant(
          pos, altBases, strand, cds, cdsFragment, variantEffectBuilder);

      // TODO deduplicate
      // calc hgvs
      CodonVariant codonVariant =
          getReferenceSequenceCodon(pos, altBases, strand, cds, cdsFragment);

      int codonPos = getCodonPos(pos, strand, cdsFragment);
      String hgvsP =
          cds.proteinId()
              + ":p."
              + (codonVariant.ref().isStopCodon()
                  ? "Ter"
                  : codonVariant.ref().getAminoAcid().getTerm())
              + (((codingPos - codonPos) / 3) + 1)
              + (codonVariant.alt().isStopCodon()
                  ? "Ter"
                  : codonVariant.alt().getAminoAcid() != codonVariant.ref().getAminoAcid()
                      ? codonVariant.alt().getAminoAcid().getTerm()
                      : "%3D"); // TODO encoding should happen in HTSJDK?
      variantEffectBuilder.hgvsP(hgvsP);
    }

    // calc hgvsC
    char ref = getBase(refBases, strand);
    char alt = getBase(altBases, strand);
    String hgvsC = transcript.getId() + ":c." + codingPos + ref + ">" + alt;
    variantEffectBuilder.hgvsC(hgvsC);
  }

  /**
   * Annotate a start codon variant.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000318">SO:0000318</a>
   */
  private void annotateCodingSequenceStartCodonVariant(
      int pos,
      Strand strand,
      Transcript transcript,
      int transcriptPos,
      Cds.Fragment cdsFragment,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    // TODO start codon can be different from 'ATG'? see 'Start Codon Selection in Eukaryotes'
    // in https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4705826/
    variantEffectBuilder.consequence(
        Consequence.START_LOST); // start codon must be 'ATG' so any variant implies a start lost

    int codonPos = getCodonPos(pos, strand, cdsFragment);
    String hgvsP =
        transcript.getCds().proteinId()
            + ":p."
            + Codon.ATG.getAminoAcid().getTerm()
            + (((transcriptPos - codonPos) / 3) + 1)
            + "?";
    variantEffectBuilder.hgvsP(hgvsP);
  }

  private boolean isStopCodonVariant(int pos, Strand strand, Cds cds) {
    Cds.Fragment cdsFragment = cds.fragments()[cds.fragments().length - 1];
    return switch (strand) {
      case POSITIVE -> cdsFragment.getStop() - pos < Codon.NR_NUCLEOTIDES;
      case NEGATIVE -> pos - cdsFragment.getStart() < Codon.NR_NUCLEOTIDES;
    };
  }

  private boolean isStartCodonVariant(int pos, Strand strand, Cds cds) {
    Cds.Fragment cdsFragment = cds.fragments()[0];
    return switch (strand) {
      case POSITIVE -> pos - cdsFragment.getStart() - cdsFragment.getPhase() < Codon.NR_NUCLEOTIDES;
      case NEGATIVE -> cdsFragment.getStop() - pos - cdsFragment.getPhase() < Codon.NR_NUCLEOTIDES;
    };
  }

  private static char getBase(byte[] bases, Strand strand) {
    return (char)
        switch (strand) {
          case POSITIVE -> bases[0];
          case NEGATIVE -> getComplementaryBase(bases[0]);
        };
  }

  // possible stop codons: TAG, TAA, TGA

  /**
   * Annotate a stop codon variant.
   *
   * @see <a
   *     href="http://sequenceontology.org/browser/current_release/term/SO:0000319">SO:0000319</a>
   */
  private void annotateCodingSequenceStopCodonVariant(
      int pos, CodonVariant codonVariant, VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    Consequence consequence =
        codonVariant.alt.isStopCodon() ? Consequence.STOP_RETAINED_VARIANT : Consequence.STOP_LOST;
    variantEffectBuilder.consequence(consequence);
  }

  private void annotateCodingSequenceCodonVariant(
      int pos,
      byte[] alt,
      Strand strand,
      Cds cds,
      Cds.Fragment cdsFragment,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    CodonVariant codonVariant = getReferenceSequenceCodon(pos, alt, strand, cds, cdsFragment);

    Consequence consequence;
    if (codonVariant.alt().isStopCodon()) {
      consequence = Consequence.STOP_GAINED;
    } else {
      AminoAcid refAminoAcid = codonVariant.ref().getAminoAcid();
      AminoAcid altAminoAcid = codonVariant.alt().getAminoAcid();
      consequence =
          refAminoAcid != altAminoAcid
              ? Consequence.MISSENSE_VARIANT
              : Consequence.SYNONYMOUS_VARIANT;
    }
    variantEffectBuilder.consequence(consequence);
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
      Strand strand,
      Transcript transcript,
      Cds cds,
      int exonIndex,
      Exon exon,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    boolean isFivePrimeUtrVariant = isFivePrimeUtrVariant(pos, strand, cds);
    if (isFivePrimeUtrVariant) {
      annotateFivePrimeUtrVariant(
          pos, refBases, altBases, strand, transcript, cds, exonIndex, exon, variantEffectBuilder);
    } else {
      annotateThreePrimeUtrVariant(
          pos, refBases, altBases, strand, transcript, cds, exonIndex, exon, variantEffectBuilder);
    }
  }

  private static boolean isFivePrimeUtrVariant(int pos, Strand strand, Cds cds) {
    return switch (strand) {
      case POSITIVE -> pos < cds.fragments()[0].getStart();
      case NEGATIVE -> pos > cds.fragments()[0].getStop();
    };
  }

  private void annotateFivePrimeUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      Cds cds,
      int exonIndex,
      Exon exon,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.FIVE_PRIME_UTR_VARIANT);

    // hgvs
    Cds.Fragment cdsFragment = cds.fragments()[0];
    int cdsPos;
    switch (strand) {
      case POSITIVE -> {
        if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
          cdsPos = pos - cdsFragment.getStart();
        } else {
          cdsPos = pos - exon.getStop() - 1;
          do {
            exon = transcript.getExons()[++exonIndex];
            if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
              cdsPos += exon.getStart() - cdsFragment.getStart();
              break;
            } else {
              cdsPos -= exon.getLength();
            }
          } while (true);
        }
      }
      case NEGATIVE -> {
        if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
          cdsPos = cdsFragment.getStop() - pos;
        } else {
          cdsPos = exon.getStart() - pos - 1;
          do {
            exon = transcript.getExons()[++exonIndex];
            if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
              cdsPos += cdsFragment.getStop() - exon.getStop();
              break;
            } else {
              cdsPos -= exon.getLength();
            }
          } while (true);
        }
      }
      default -> throw new IllegalStateException();
    }

    char ref = getBase(refBases, strand);
    char alt = getBase(altBases, strand);
    String hgvsC = transcript.getId() + ":c." + cdsPos + ref + ">" + alt;
    variantEffectBuilder.hgvsC(hgvsC);
  }

  private void annotateThreePrimeUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      Cds cds,
      int exonIndex,
      Exon exon,
      VariantEffect.VariantEffectBuilder variantEffectBuilder) {
    variantEffectBuilder.consequence(Consequence.THREE_PRIME_UTR_VARIANT);

    // hgvs
    Cds.Fragment cdsFragment = cds.fragments()[cds.fragments().length - 1];
    int cdsPos;
    switch (strand) {
      case POSITIVE -> {
        if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
          cdsPos = pos - cdsFragment.getStop();
        } else {
          cdsPos = pos - exon.getStart();
          do {
            exon = transcript.getExons()[--exonIndex];
            if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
              cdsPos += exon.getStart() - cdsFragment.getStop();
              break;
            } else {
              cdsPos += exon.getLength();
            }
          } while (true);
        }
      }
      case NEGATIVE -> {
        if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
          cdsPos = cdsFragment.getStart() - pos;
        } else {
          cdsPos = exon.getStop() - pos + 1;
          do {
            exon = transcript.getExons()[--exonIndex];
            if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
              cdsPos += cdsFragment.getStart() - exon.getStart();
              break;
            } else {
              cdsPos += exon.getLength();
            }
          } while (true);
        }
      }
      default -> throw new IllegalStateException();
    }

    char ref = getBase(refBases, strand);
    char alt = getBase(altBases, strand);
    String hgvsC = transcript.getId() + ":c.*" + cdsPos + ref + ">" + alt;
    variantEffectBuilder.hgvsC(hgvsC);
  }

  /**
   * @return relative position 0, 1 or 2 in the codon
   */
  private int getCodonPos(int pos, Strand strand, Cds.Fragment cds) {
    return switch (strand) {
      case Strand.POSITIVE -> (pos - cds.getStart() - cds.getPhase()) % 3;
      case Strand.NEGATIVE -> (cds.getStop() - pos - cds.getPhase()) % 3;
    };
  }

  private CodonVariant getReferenceSequenceCodon(
      int pos, byte[] alt, Strand strand, Cds cds, Cds.Fragment cdsFragment) {
    int codonPos = getCodonPos(pos, strand, cdsFragment);
    char[] refSequence =
        switch (strand) {
          case Strand.POSITIVE -> {
            // codon can be spliced
            if (pos - codonPos < cdsFragment.getStart()) {
              // FIXME
              throw new RuntimeException();
            } else if (pos - codonPos + 2 > cdsFragment.getStop()) {
              // FIXME
              throw new RuntimeException();
            } else {
              yield annotationDb.getSequence(pos - codonPos, pos - codonPos + 2, strand);
            }
          }
          case Strand.NEGATIVE -> {
            if (pos + codonPos > cdsFragment.getStop()) {
              // FIXME
              throw new RuntimeException();
            } else if (pos + codonPos - 2 < cdsFragment.getStart()) {
              Cds.Fragment otherCdsFragment = null;
              for (int i = 0; i < cds.fragments().length; ++i) {
                if (cds.fragments()[i].equals(cdsFragment)) {
                  otherCdsFragment = cds.fragments()[i + 1];
                  break;
                }
              }
              char[] first =
                  annotationDb.getSequence(pos + codonPos, cdsFragment.getStart(), strand);
              char[] second =
                  annotationDb.getSequence(
                      otherCdsFragment.getStop(),
                      otherCdsFragment.getStop() + codonPos - 2 + first.length,
                      strand);

              char[] both = Arrays.copyOf(first, first.length + second.length);
              System.arraycopy(second, 0, both, first.length, second.length);
              yield both;
            } else {
              yield annotationDb.getSequence(pos + codonPos, pos + codonPos - 2, strand);
            }
          }
        };

    if (refSequence == null) throw new RuntimeException();

    char[] altSequence = new char[3];
    System.arraycopy(refSequence, 0, altSequence, 0, 3);

    altSequence[codonPos] =
        switch (strand) {
          case POSITIVE -> (char) alt[0];
          case NEGATIVE -> (char) getComplementaryBase(alt[0]);
        };

    return new CodonVariant(from(refSequence), from(altSequence));
  }

  private record CodonVariant(Codon ref, Codon alt) {}
}
