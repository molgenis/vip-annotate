package org.molgenis.vcf.annotate;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.annotate.model.Codon.from;

import htsjdk.variant.variantcontext.Allele;
import java.util.List;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariantConsequenceCalculator {
  private static final Logger LOGGER = LoggerFactory.getLogger(VariantConsequenceCalculator.class);
  private final GenomeAnnotationDb genomeAnnotationDb;

  public VariantConsequenceCalculator(GenomeAnnotationDb genomeAnnotationDb) {
    this.genomeAnnotationDb = requireNonNull(genomeAnnotationDb);
  }

  // FIXME only works for SNPs
  // FIXME only works for genes on '+' strand
  public Consequence determineConsequenceTranscriptVariant(
      Chromosome chr, int pos, Allele ref, Allele alt, Transcript transcript) {
    List<Exon> exons = transcript.findExons(pos, pos);

    Consequence consequence;
    if (!exons.isEmpty()) {
      consequence = determineConsequenceExon(chr, pos, ref, alt, transcript, exons.getFirst());
    } else {
      consequence = determineConsequenceIntron();
    }
    return consequence;
  }

  private Consequence determineConsequenceExon(
      Chromosome chromosome, int pos, Allele ref, Allele alt, Transcript transcript, Exon exon) {
    List<Cds> codingSequences = transcript.findCds(pos, pos);
    if (!codingSequences.isEmpty()) {
      return determineConsequenceExonCdsRegion(
          chromosome, pos, ref, alt, transcript, exon, codingSequences.getFirst());
    } else {
      return determineConsequenceExonUtrRegion(chromosome, pos, ref, alt, transcript, exon);
    }
  }

  private Consequence determineConsequenceExonCdsRegion(
      Chromosome chromosome,
      int pos,
      Allele ref,
      Allele alt,
      Transcript transcript,
      Exon exon,
      Cds cds) {
    CodonRegion codonRegion = getCodonRegion(pos, cds);
    return switch (codonRegion) {
      case START_CODON ->
          determineConsequenceExonCdsRegionStartCodon(chromosome, pos, ref, alt, cds);
      case CODON ->
          determineConsequenceExonCdsRegionCodon(chromosome, pos, ref, alt, transcript, cds, exon);
      case STOP_CODON -> determineConsequenceExonCdsRegionStopCodon(pos, ref, alt, cds);
    };
  }

  // TODO start codon can be different from 'ATG'? see 'Start Codon Selection in Eukaryotes' in
  // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4705826/
  private Consequence determineConsequenceExonCdsRegionStartCodon(
      Chromosome chr, int pos, Allele ref, Allele alt, Cds cds) {
    // start codon must be 'ATG' so any variant implies a start lost
    return Consequence.START_LOST;
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
      Chromosome chromosome,
      int pos,
      Allele ref,
      Allele alt,
      Transcript transcript,
      Cds cds,
      Exon exon) {
    CodonVariant codonVariant = getReferenceSequenceCodon(chromosome, pos, alt, transcript, cds);

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

  private Consequence determineConsequenceExonUtrRegion(
      Chromosome chromosome, int pos, Allele ref, Allele alt, Transcript transcript, Exon exon) {
    return exon.getStart() == transcript.getStart()
        ? (transcript.getStrand() == Strand.PLUS
            ? Consequence.FIVE_PRIME_UTR_VARIANT
            : Consequence.THREE_PRIME_UTR_VARIANT)
        : (transcript.getStrand() == Strand.PLUS
            ? Consequence.THREE_PRIME_UTR_VARIANT
            : Consequence.FIVE_PRIME_UTR_VARIANT);
  }

  private Consequence determineConsequenceIntron() {
    // TODO determine child class, e.g. splice_site_variant
    return Consequence.INTRON_VARIANT;
  }

  private CodonVariant getReferenceSequenceCodon(
      Chromosome chromosome, int pos, Allele alt, Transcript transcript, Cds cds) {
    int start = cds.getStart();
    int codonPos = (pos - start) % 3;

    byte[] refCodon =
        genomeAnnotationDb.findSequence(chromosome, pos - codonPos, pos - codonPos + 2);
    byte[] altCodon = new byte[3];
    System.arraycopy(refCodon, 0, altCodon, 0, 3);
    altCodon[codonPos] = alt.getBases()[0];
    return new CodonVariant(
        from(refCodon, transcript.getStrand()), Codon.from(altCodon, transcript.getStrand()));
  }

  private record CodonVariant(Codon ref, Codon alt) {}
}
