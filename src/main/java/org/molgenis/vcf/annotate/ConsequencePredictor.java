package org.molgenis.vcf.annotate;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.annotate.model.Codon.from;

import htsjdk.variant.variantcontext.Allele;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.model.*;

public class ConsequencePredictor {
  private final AnnotationDb annotationDb;

  public ConsequencePredictor(AnnotationDb annotationDb) {
    this.annotationDb = requireNonNull(annotationDb);
  }

  // FIXME only works for SNPs
  // FIXME only works for genes on '+' strand
  public Consequence predictConsequence(
      int pos, Allele ref, Allele alt, Strand strand, Transcript transcript) {
    Exon exon = transcript.findAnyExon(pos - 2, pos + 2);

    Consequence consequence;
    if (exon != null) {
      if (pos < exon.getStart() && exon.getStart() != transcript.getStart()) {
        consequence = Consequence.SPLICE_ACCEPTOR_VARIANT;
      } else if (pos >= exon.getStop() && exon.getStop() != transcript.getStop()) {
        consequence = Consequence.SPLICE_DONOR_VARIANT;
      } else {
        consequence = predictExonConsequence(pos, ref, alt, strand, transcript, exon);
      }
    } else {
      consequence = Consequence.INTRON_VARIANT;
    }
    return consequence;
  }

  private Consequence predictExonConsequence(
      int pos, Allele ref, Allele alt, Strand strand, Transcript transcript, Exon exon) {
    Cds cds = transcript.findAnyCds(pos, pos);
    if (cds != null) {
      return predictCdsConsequence(pos, ref, alt, strand, cds);
    } else {
      return predictUtrConsequence(strand, transcript, exon);
    }
  }

  private Consequence predictCdsConsequence(
      int pos, Allele ref, Allele alt, Strand strand, Cds cds) {
    CodonRegion codonRegion = getCodonRegion(pos, cds);
    return switch (codonRegion) {
      case START_CODON ->
          // TODO start codon can be different from 'ATG'? see 'Start Codon Selection in Eukaryotes'
          // in https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4705826/
          Consequence.START_LOST; // start codon must be 'ATG' so any variant implies a start lost
      case CODON -> determineConsequenceExonCdsRegionCodon(pos, alt, strand, cds);
      case STOP_CODON -> determineConsequenceExonCdsRegionStopCodon(pos, ref, alt, cds);
    };
  }

  // possible stop codons: TAG, TAA, TGA
  private Consequence determineConsequenceExonCdsRegionStopCodon(
      int pos, Allele ref, Allele alt, Cds cds) {
    int codonPos = Math.toIntExact(cds.getStop() - pos);
    return switch (codonPos) {
      case 0 -> Consequence.STOP_LOST;
      case 1, 2 -> {
        byte altBase = alt.getBases()[0];
        yield altBase == 'A' || (altBase == 'G' && ref.getBases()[0] != 'G')
            ? Consequence.STOP_RETAINED_VARIANT
            : Consequence.STOP_LOST;
      }
      default -> throw new RuntimeException();
    };
  }

  private Consequence determineConsequenceExonCdsRegionCodon(
      int pos, Allele alt, Strand strand, Cds cds) {
    CodonVariant codonVariant = getReferenceSequenceCodon(pos, alt, strand, cds);

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
    return consequence;
  }

  /**
   * FIXME not always possible to determine start and stop codons, see README_GFF3.txt:
   *
   * <p>Start and stop codons --------------------- start_codon and stop_codon features are not
   * explicitly annotated, but can be inferred from the beginning and end of the CDS feature, if
   * that CDS feature is not partial on the end in question. Partialness is represented by
   * start_range and end_range attributes in NCBI's GFF3 files, using a format adapted from GVF.
   *
   * <p>More specifically: 1) the CDS is 5' partial and does not include a start codon if: a) the
   * first CDS row is on the + strand and has a start_range=.,### attribute. b) the first CDS row is
   * on the - strand and has an end_range=###,. attribute.
   *
   * <p>2) the CDS is 3' partial and does not include a stop codon if: a) the last CDS row is on the
   * + strand and has an end_range=###,. attribute. b) the last CDS row is on the - strand and has a
   * start_range=.,### attribute.
   *
   * <p>If those conditions are not met, then you can infer the start_codon and stop_codon position
   * from the first or last 3 bp of the CDS feature.
   */
  private static CodonRegion getCodonRegion(int pos, Cds cds) {
    CodonRegion codonRegion;
    if (pos - cds.getStart() < 3L) {
      codonRegion = CodonRegion.START_CODON;
    } else if (cds.getStop() - pos < 3L) {
      codonRegion = CodonRegion.STOP_CODON;
    } else {
      codonRegion = CodonRegion.CODON;
    }
    return codonRegion;
  }

  private Consequence predictUtrConsequence(Strand strand, Transcript transcript, Exon exon) {
    return exon.getStart() == transcript.getStart()
        ? (strand == Strand.PLUS
            ? Consequence.FIVE_PRIME_UTR_VARIANT
            : Consequence.THREE_PRIME_UTR_VARIANT)
        : (strand == Strand.PLUS
            ? Consequence.THREE_PRIME_UTR_VARIANT
            : Consequence.FIVE_PRIME_UTR_VARIANT);
  }

  private CodonVariant getReferenceSequenceCodon(int pos, Allele alt, Strand strand, Cds cds) {
    int start = cds.getStart() + cds.getPhase();
    int codonPos = (pos - start) % 3;

    byte[] refCodon = annotationDb.findSequence(pos - codonPos, pos - codonPos + 2);
    byte[] altCodon = new byte[3];
    System.arraycopy(refCodon, 0, altCodon, 0, 3);
    altCodon[codonPos] = alt.getBases()[0];
    return new CodonVariant(from(refCodon, strand), Codon.from(altCodon, strand));
  }

  private record CodonVariant(Codon ref, Codon alt) {}
}
