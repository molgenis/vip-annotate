package org.molgenis.vcf.annotate.annotator.effect;

import org.molgenis.vcf.annotate.db.effect.model.*;

/**
 * The HGVS Nomenclature is an internationally-recognized standard for the description of DNA, RNA,
 * and protein sequence variants. It is used to convey variants in clinical reports and to share
 * variants in publications and databases.
 *
 * @see <a href="https://hgvs-nomenclature.org/stable/">HGVS Nomenclature</a>
 */
public class SnpHgvsDescriber {
  public record Hgvs(String hgvsC, String hgvsP) {}

  public static String calculateHgvsCIntronVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      FuryFactory.Exon fivePrimeExon,
      int threePrimeExonIndex,
      FuryFactory.Exon threePrimeExon) {
    if (transcript.getCds() != null) {
      return calculateIntronVariantCodingDnaHgvsC(
          pos,
          refBases,
          altBases,
          strand,
          transcript,
          fivePrimeExon,
          threePrimeExonIndex,
          threePrimeExon);
    } else {
      return calculateIntronVariantNonCodingDnaHgvsC(
          pos,
          refBases,
          altBases,
          strand,
          transcript,
          threePrimeExonIndex,
          fivePrimeExon,
          threePrimeExon);
    }
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
  private static String calculateIntronVariantCodingDnaHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      FuryFactory.Exon fivePrimeExon,
      int threePrimeExonIndex,
      FuryFactory.Exon threePrimeExon) {

    FuryFactory.Cds.Fragment[] cdsFragments = transcript.getCds().fragments();
    int exonPos;
    int intronPos;
    String codingReferenceSequencePos;
    switch (strand) {
      case POSITIVE -> {
        int intronCenter =
            fivePrimeExon.getStop() + ((threePrimeExon.getStart() - fivePrimeExon.getStop()) / 2);

        int exonIndex;
        if (pos <= intronCenter) {
          exonPos = fivePrimeExon.getStop();
          exonIndex = threePrimeExonIndex - 1;
        } else {
          exonPos = threePrimeExon.getStart();
          exonIndex = threePrimeExonIndex;
        }

        if (exonPos < cdsFragments[0].getStart()) {
          // 5' ÚTR
          codingReferenceSequencePos =
              String.valueOf(getCdsPosFivePrimeUtr(exonPos, strand, transcript, exonIndex));
        } else if (exonPos > cdsFragments[cdsFragments.length - 1].getStop()) {
          // 3' ÚTR
          codingReferenceSequencePos =
              "*" + getCdsPosThreePrimeUtr(exonPos, strand, transcript, exonIndex);
        } else {
          // protein coding region
          int cdsFragmentIndex = transcript.getCds().findAnyFragmentId(exonPos, exonPos);
          if (cdsFragmentIndex == -1) throw new RuntimeException();
          codingReferenceSequencePos =
              String.valueOf(
                  getCdsPosProteinCodingRegion(
                      exonPos, strand, transcript.getCds(), cdsFragmentIndex));
        }

        intronPos = pos - exonPos;
      }
      case NEGATIVE -> {
        int intronCenter =
            threePrimeExon.getStop() + ((fivePrimeExon.getStart() - threePrimeExon.getStop()) / 2);
        int exonIndex;
        if (pos <= intronCenter) {
          exonPos = threePrimeExon.getStop();
          exonIndex = threePrimeExonIndex;
        } else {
          exonPos = fivePrimeExon.getStart();
          exonIndex = threePrimeExonIndex - 1;
        }

        if (exonPos > cdsFragments[0].getStop()) {
          // 5' ÚTR
          codingReferenceSequencePos =
              String.valueOf(getCdsPosFivePrimeUtr(exonPos, strand, transcript, exonIndex));
        } else if (exonPos < cdsFragments[cdsFragments.length - 1].getStart()) {
          // 3' ÚTR
          codingReferenceSequencePos =
              "*" + getCdsPosThreePrimeUtr(exonPos, strand, transcript, exonIndex);
        } else {
          // protein coding region
          int cdsFragmentIndex = transcript.getCds().findAnyFragmentId(exonPos, exonPos);
          if (cdsFragmentIndex == -1) throw new RuntimeException();
          codingReferenceSequencePos =
              String.valueOf(
                  getCdsPosProteinCodingRegion(
                      exonPos, strand, transcript.getCds(), cdsFragmentIndex));
        }

        intronPos = exonPos - pos;
      }
      default -> throw new IllegalStateException("Unexpected value: " + strand);
    }

    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(transcript.getId()).append(":c.");

    stringBuilder.append(codingReferenceSequencePos);
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
  private static String calculateIntronVariantNonCodingDnaHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int threePrimeExonIndex,
      FuryFactory.Exon fivePrimeExon,
      FuryFactory.Exon threePrimeExon) {
    int intronPos;
    int transcriptPos = 0;
    switch (strand) {
      case POSITIVE -> {
        int intronCenter =
            fivePrimeExon.getStop() + ((threePrimeExon.getStart() - fivePrimeExon.getStop()) / 2);
        int exonPos;
        if (pos <= intronCenter) {
          exonPos = fivePrimeExon.getStop();

          for (FuryFactory.Exon exon : transcript.getExons()) {
            if (exon.isOverlapping(exonPos, exonPos)) {
              transcriptPos += exon.getLength();
              break;
            } else {
              transcriptPos += exon.getLength();
            }
          }
        } else {
          exonPos = threePrimeExon.getStart();

          for (FuryFactory.Exon exon : transcript.getExons()) {
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
          for (FuryFactory.Exon exon : transcript.getExons()) {
            if (exon.isOverlapping(exonPos, exonPos)) {
              transcriptPos += 1;
              break;
            } else {
              transcriptPos += exon.getLength();
            }
          }
        } else {
          exonPos = fivePrimeExon.getStart();
          for (FuryFactory.Exon exon : transcript.getExons()) {
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

    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(transcript.getId()).append(":n.").append(transcriptPos);
    if (intronPos >= 0) {
      stringBuilder.append('+');
    }
    stringBuilder.append(intronPos).append(ref).append('>').append(alt);
    return stringBuilder.toString();
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
  public static String calculateNonCodingTranscriptExonVariantHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int exonIndex,
      FuryFactory.Exon exon) {
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
    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    stringBuilder.append(ref).append('>').append(alt);
    return stringBuilder.toString();
  }

  private static String calculateHgvsP(
      FuryFactory.Cds cds, CodonVariant codonVariant, int codingPos, int codonPos) {
    String hgvsP;
    int aaPosition = ((codingPos - codonPos) / 3) + 1;
    if (aaPosition > 1) {
      if (codonVariant.ref().isStopCodon() && !codonVariant.alt().isStopCodon()) {
        hgvsP =
            cds.proteinId()
                + ":p."
                + "Ter"
                + aaPosition
                + codonVariant.alt().getAminoAcid().getTerm()
                + "extTer"
                + "?"; // FIXME add extension_length (The number of amino acids added beyond the
        // original Ter)
      } else {
        hgvsP =
            cds.proteinId()
                + ":p."
                + (codonVariant.ref().isStopCodon()
                    ? "Ter"
                    : codonVariant.ref().getAminoAcid().getTerm())
                + aaPosition
                + (codonVariant.ref().getAminoAcid() != codonVariant.alt().getAminoAcid()
                    ? (codonVariant.alt().isStopCodon()
                        ? "Ter"
                        : codonVariant.alt().getAminoAcid().getTerm())
                    : "%3D");
      }
    } else {
      hgvsP =
          cds.proteinId() + ":p." + codonVariant.ref().getAminoAcid().getTerm() + aaPosition + "?";
    }
    return hgvsP;
  }

  public static String calculateHgvsCFivePrimeUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int overlappingExonIndex) {
    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    int cdsPos = getCdsPosFivePrimeUtr(pos, strand, transcript, overlappingExonIndex);
    return transcript.getId() + ":c." + cdsPos + ref + ">" + alt;
  }

  public static String calculateHgvsCThreePrimeUtrVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int overlappingExonIndex) {
    int cdsPos = getCdsPosThreePrimeUtr(pos, strand, transcript, overlappingExonIndex);

    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    return transcript.getId() + ":c.*" + cdsPos + ref + ">" + alt;
  }

  public static Hgvs calculateHgvsCodingSequenceVariant(
      int pos,
      byte[] refBases,
      byte[] altBases,
      FuryFactory.Strand strand,
      FuryFactory.Transcript transcript,
      int cdsFragmentId,
      CodonVariant codonVariant,
      int codonPos) {
    int cdsPos = getCdsPosProteinCodingRegion(pos, strand, transcript.getCds(), cdsFragmentId);

    // c.
    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    String hgvsC = transcript.getId() + ":c." + cdsPos + ref + ">" + alt;

    // p.
    String hgvsP = calculateHgvsP(transcript.getCds(), codonVariant, cdsPos, codonPos);

    return new Hgvs(hgvsC, hgvsP);
  }

  /**
   * @see <a
   *     href="https://hgvs-nomenclature.org/stable/background/numbering/#coding-dna-reference-sequences">untranslated
   *     region (UTR)</a>
   */
  private static int getCdsPosFivePrimeUtr(
      int pos, FuryFactory.Strand strand, FuryFactory.Transcript transcript, int exonIndex) {
    // hgvs
    FuryFactory.Exon exon = transcript.getExons()[exonIndex];
    FuryFactory.Cds cds = transcript.getCds();
    FuryFactory.Cds.Fragment cdsFragment = cds.fragments()[0];
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
    return cdsPos;
  }

  /**
   * @see <a
   *     href="https://hgvs-nomenclature.org/stable/background/numbering/#coding-dna-reference-sequences">protein
   *     coding region</a>
   */
  private static int getCdsPosProteinCodingRegion(
      int pos, FuryFactory.Strand strand, FuryFactory.Cds cds, int overlappingCdsFragmentId) {
    FuryFactory.Cds.Fragment overlappingCdsFragment = cds.fragments()[overlappingCdsFragmentId];

    int codingReferenceSequencePos =
        switch (strand) {
          case POSITIVE -> pos - overlappingCdsFragment.getStart() + 1;
          case NEGATIVE -> overlappingCdsFragment.getStop() - pos + 1;
        };

    for (int i = 0; i < overlappingCdsFragmentId; i++) {
      codingReferenceSequencePos += cds.fragments()[i].getLength();
    }

    return codingReferenceSequencePos;
  }

  /**
   * @see <a
   *     href="https://hgvs-nomenclature.org/stable/background/numbering/#coding-dna-reference-sequences">untranslated
   *     region (UTR)</a>
   */
  private static int getCdsPosThreePrimeUtr(
      int pos, FuryFactory.Strand strand, FuryFactory.Transcript transcript, int exonIndex) {
    FuryFactory.Exon exon = transcript.getExons()[exonIndex];
    FuryFactory.Cds cds = transcript.getCds();
    FuryFactory.Cds.Fragment cdsFragment = cds.fragments()[cds.fragments().length - 1];
    int cdsPos;
    switch (strand) {
      case POSITIVE -> {
        if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
          cdsPos = pos - cdsFragment.getStop();
        } else {
          cdsPos = pos - exon.getStart() + 1;
          do {
            exon = transcript.getExons()[--exonIndex];
            if (cdsFragment.isOverlapping(exon.getStart(), exon.getStop())) {
              cdsPos += exon.getStop() - cdsFragment.getStop();
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
    return cdsPos;
  }
}
