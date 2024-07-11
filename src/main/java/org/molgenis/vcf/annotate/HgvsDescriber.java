package org.molgenis.vcf.annotate;

import org.molgenis.vcf.annotate.db.model.Cds;
import org.molgenis.vcf.annotate.db.model.Exon;
import org.molgenis.vcf.annotate.db.model.Strand;
import org.molgenis.vcf.annotate.db.model.Transcript;
import org.molgenis.vcf.annotate.model.Codon;
import org.molgenis.vcf.annotate.util.CodonVariant;
import org.molgenis.vcf.annotate.util.SequenceUtils;

/**
 * The HGVS Nomenclature is an internationally-recognized standard for the description of DNA, RNA,
 * and protein sequence variants. It is used to convey variants in clinical reports and to share
 * variants in publications and databases.
 *
 * @see <a href="https://hgvs-nomenclature.org/stable/">HGVS Nomenclature</a>
 */
public class HgvsDescriber {
  static String calculateIntronVariantHgvsC(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      Exon fivePrimeExon,
      int threePrimeExonIndex,
      Exon threePrimeExon) {
    if (transcript.getCds() != null) {
      return calculateIntronVariantCodingDnaHgvsC(
          pos, refBases, altBases, strand, transcript, fivePrimeExon, threePrimeExon);
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

    int cdsFragmentIndex = transcript.getCds().findAnyFragmentId(exonPos, exonPos);
    Cds.Fragment cdsFragment = transcript.getCds().fragments()[cdsFragmentIndex];
    String codingReferenceSequencePos = getCdsPos(exonPos, strand, transcript, cdsFragment);

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
  static String calculateNonCodingTranscriptExonVariantHgvsC(
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
    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    stringBuilder.append(ref).append('>').append(alt);
    return stringBuilder.toString();
  }

  private static String getCdsPos(
      int pos, Strand strand, Transcript transcript, Cds.Fragment cdsFragment) {
    String nucleotideNumber;
    if (cdsFragment != null) {
      int codingReferenceSequencePos = 0;
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
      nucleotideNumber = String.valueOf(codingReferenceSequencePos);
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
            // 5' UTR
            int codingReferenceSequencePos = 0;
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
            nucleotideNumber = String.valueOf(codingReferenceSequencePos);
          } else {
            // 3' UTR
            cdsFragment =
                transcript.getCds().fragments()[transcript.getCds().fragments().length - 1];

            int codingReferenceSequencePos = 0;
            Exon exon = exons[exonIndex];
            while (!exon.isOverlapping(cdsFragment.getStart(), cdsFragment.getStop())) {
              if (exon.isOverlapping(pos, pos)) {
                codingReferenceSequencePos += pos - exon.getStart() + 1;
              } else {
                codingReferenceSequencePos += exon.getLength();
              }
              exon = transcript.getExons()[--exonIndex];
            }
            codingReferenceSequencePos += exon.getStop() - cdsFragment.getStop();
            nucleotideNumber = "*" + codingReferenceSequencePos;
          }
        }
        case NEGATIVE -> {
          if (pos > cdsFragment.getStop()) {
            // 5' UTR
            int codingReferenceSequencePos = 0;
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
            nucleotideNumber = String.valueOf(codingReferenceSequencePos);
          } else {
            // 3' UTR
            // FIXME implement
            nucleotideNumber = "*<something>";
          }
        }
        default -> throw new IllegalArgumentException();
      }
    }

    return nucleotideNumber;
  }

  static String calculateHgvsP(Cds cds, CodonVariant codonVariant, String codingPos, int codonPos) {
    String hgvsP =
        cds.proteinId()
            + ":p."
            + (codonVariant.ref().isStopCodon()
                ? "Ter"
                : codonVariant.ref().getAminoAcid().getTerm())
            + (((Integer.parseInt(codingPos) - codonPos) / 3) + 1)
            + (codonVariant.alt().isStopCodon()
                ? "Ter"
                : codonVariant.alt().getAminoAcid().getTerm());
    return hgvsP;
  }

  static String calculateStartCodonVariantHgvsP(
      Transcript transcript, String transcriptPos, int codonPos) {
    String hgvsP =
        transcript.getCds().proteinId()
            + ":p."
            + Codon.ATG.getAminoAcid().getTerm()
            + (((Integer.parseInt(transcriptPos) - codonPos) / 3) + 1)
            + "?";
    return hgvsP;
  }

  static int getCdsPosFivePrimeUtr(
      int pos, Strand strand, Transcript transcript, Cds cds, int exonIndex, Exon exon) {
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
    return cdsPos;
  }

  static String calculateHgvsCFivePrimeUtr(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      Cds cds,
      int exonIndex,
      Exon exon) {
    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    int cdsPos = getCdsPosFivePrimeUtr(pos, strand, transcript, cds, exonIndex, exon);
    String hgvsC = transcript.getId() + ":c." + cdsPos + ref + ">" + alt;
    return hgvsC;
  }

  static String calculateHgvsCThreePrime(
      int pos,
      byte[] refBases,
      byte[] altBases,
      Strand strand,
      Transcript transcript,
      Cds cds,
      int exonIndex,
      Exon exon) {
    int cdsPos = getCdsPosThreePrimeUtr(pos, strand, transcript, cds, exonIndex, exon);

    char ref = SequenceUtils.getBase(refBases, strand);
    char alt = SequenceUtils.getBase(altBases, strand);
    String hgvsC = transcript.getId() + ":c.*" + cdsPos + ref + ">" + alt;
    return hgvsC;
  }

  private static int getCdsPosThreePrimeUtr(
      int pos, Strand strand, Transcript transcript, Cds cds, int exonIndex, Exon exon) {
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
    return cdsPos;
  }

  public static String calculateHgvsC() {
    throw new RuntimeException();
  }
}
