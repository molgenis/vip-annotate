package org.molgenis.vcf.annotate;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.annotate.model.Codon.from;

import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.model.*;

public class VariantTranscriptConsequenceAnnotator {
  private final AnnotationDb annotationDb;

  public VariantTranscriptConsequenceAnnotator(AnnotationDb annotationDb) {
    this.annotationDb = requireNonNull(annotationDb);
  }

  private boolean isSpliceAcceptorVariant(int pos, Strand strand, Exon exon) {
    return switch (strand) {
      case POSITIVE -> exon.getStart() - pos == 1 || exon.getStart() - pos == 2;
      case NEGATIVE -> pos - exon.getStop() == 1 || pos - exon.getStop() == 2;
    };
  }

  private boolean isSpliceDonorVariant(int pos, Strand strand, Exon exon) {
    return switch (strand) {
      case POSITIVE -> pos - exon.getStop() == 1 || pos - exon.getStop() == 2;
      case NEGATIVE -> exon.getStart() - pos == 1 || exon.getStart() - pos == 2;
    };
  }

  // FIXME only works for SNPs
  public TranscriptAnnotation annotate(
      int pos, byte[] ref, byte[] alt, Strand strand, Transcript transcript) {
    TranscriptAnnotation transcriptAnnotation = null;

    Exon[] exons = transcript.getExons();

    for (int i = 0; i < exons.length; i++) {
      Exon exon = exons[i];
      if ((strand == Strand.POSITIVE && pos < exon.getStart() - 2)
          || (strand == Strand.NEGATIVE && pos > exon.getStop() + 2)) {
        transcriptAnnotation =
            TranscriptAnnotation.builder()
                .consequence(Consequence.INTRON_VARIANT)
                .intron(
                    strand == Strand.POSITIVE
                        ? i + "/" + (exons.length - 1)
                        : (exons.length - i + 1) + "/" + (exons.length - 1)) // FIXME?
                .build();
        break;
      } else if (exon.isOverlapping(pos - 2, pos + 2)) {
        if (isSpliceAcceptorVariant(pos, strand, exon)) {
          // TODO add HGVS
          transcriptAnnotation =
              TranscriptAnnotation.builder()
                  .consequence(Consequence.SPLICE_ACCEPTOR_VARIANT)
                  .build();
        } else if (isSpliceDonorVariant(pos, strand, exon)) {
          // TODO add HGVS
          transcriptAnnotation =
              TranscriptAnnotation.builder().consequence(Consequence.SPLICE_DONOR_VARIANT).build();
        } else {
          TranscriptAnnotation.TranscriptAnnotationBuilder builder =
              predictExonConsequence(pos, ref, alt, strand, transcript, exon);
          transcriptAnnotation =
              builder
                  .exon(
                      strand == Strand.POSITIVE
                          ? i + "/" + exons.length
                          : (exons.length - i + 1) + "/" + (exons.length - 1)) // FIXME?
                  .build();
        }
        break;
      }
    }
    return transcriptAnnotation;
  }

  private TranscriptAnnotation.TranscriptAnnotationBuilder predictExonConsequence(
      int pos, byte[] refBases, byte[] altBases, Strand strand, Transcript transcript, Exon exon) {
    TranscriptAnnotation.TranscriptAnnotationBuilder builder;

    Cds cds = transcript.getCds();
    if (cds == null) {
      Consequence consequence = predictUtrConsequence(strand, transcript, exon);
      builder = TranscriptAnnotation.builder().consequence(consequence);
    } else {
      Cds.Part[] parts = cds.parts();
      if ((strand == Strand.POSITIVE
              && (pos < parts[0].getStart() || pos > parts[parts.length - 1].getStop()))
          || (strand == Strand.NEGATIVE
              && (pos > parts[0].getStop() || pos < parts[parts.length - 1].getStart()))) {
        Consequence consequence = predictUtrConsequence(strand, transcript, exon);
        builder = TranscriptAnnotation.builder().consequence(consequence);
      } else {
        // find cds and keep track of transcript pos for hgvs notation
        int transcriptPos = 0;
        Cds.Part cdsPart = null;
        for (Cds.Part part : parts) {
          cdsPart = part;

          if (strand == Strand.POSITIVE) {
            if (pos > cdsPart.getStop()) {
              transcriptPos += cdsPart.getLength();
            } else {
              transcriptPos += pos - cdsPart.getStart() + 1;
              break;
            }
          } else if (strand == Strand.NEGATIVE) {
            if (pos < cdsPart.getStart()) {
              transcriptPos += cdsPart.getLength();
            } else {
              transcriptPos += cdsPart.getStop() - pos + 1;
              break;
            }
          } else throw new RuntimeException();
        }

        byte ref = strand == Strand.POSITIVE ? refBases[0] : getComplementaryBase(refBases[0]);
        byte alt = strand == Strand.POSITIVE ? altBases[0] : getComplementaryBase(altBases[0]);
        String hgvsC =
            transcript.getId() + ":c." + transcriptPos + ((char) ref) + ">" + ((char) alt);
        String hgvsP = cds.proteinId() + ":p."; // FIXME
        Consequence consequence = predictCdsConsequence(pos, refBases, altBases, strand, cdsPart);
        builder = TranscriptAnnotation.builder().consequence(consequence).hgvsC(hgvsC).hgvsP(hgvsP);
      }
    }

    return builder;
  }

  private byte getComplementaryBase(byte refBase) {
    return switch (refBase) {
      case 'A' -> 'T';
      case 'C' -> 'G';
      case 'G' -> 'C';
      case 'T' -> 'A';
      default -> throw new IllegalArgumentException();
    };
  }

  private Consequence predictCdsConsequence(
      int pos, byte[] ref, byte[] alt, Strand strand, Cds.Part cdsPart) {
    CodonRegion codonRegion = getCodonRegion(pos, cdsPart);
    return switch (codonRegion) {
      case START_CODON ->
          // TODO start codon can be different from 'ATG'? see 'Start Codon Selection in Eukaryotes'
          // in https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4705826/
          Consequence.START_LOST; // start codon must be 'ATG' so any variant implies a start lost
      case CODON -> determineConsequenceExonCdsRegionCodon(pos, alt, strand, cdsPart);
      case STOP_CODON -> determineConsequenceExonCdsRegionStopCodon(pos, ref, alt, cdsPart);
    };
  }

  // possible stop codons: TAG, TAA, TGA
  private Consequence determineConsequenceExonCdsRegionStopCodon(
      int pos, byte[] ref, byte[] alt, Cds.Part cds) {
    int codonPos = Math.toIntExact(cds.getStop() - pos);
    return switch (codonPos) {
      case 0 -> Consequence.STOP_LOST;
      case 1, 2 -> {
        byte altBase = alt[0];
        yield altBase == 'A' || (altBase == 'G' && ref[0] != 'G')
            ? Consequence.STOP_RETAINED_VARIANT
            : Consequence.STOP_LOST;
      }
      default -> throw new RuntimeException();
    };
  }

  private Consequence determineConsequenceExonCdsRegionCodon(
      int pos, byte[] alt, Strand strand, Cds.Part cds) {
    CodonVariant codonVariant = getReferenceSequenceCodon(pos, alt, strand, cds);

    Consequence consequence;
    if (codonVariant.alt().isStopCodon()) {
      // NM_001282539.2:c.640C>T|NP_001269468.1:p.Arg214Ter
      // NM_001282538.2:c.340C>T|NP_001269467.1:p.Arg114Ter
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
  private static CodonRegion getCodonRegion(int pos, Cds.Part cds) {
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
        ? (strand == Strand.POSITIVE
            ? Consequence.FIVE_PRIME_UTR_VARIANT
            : Consequence.THREE_PRIME_UTR_VARIANT)
        : (strand == Strand.POSITIVE
            ? Consequence.THREE_PRIME_UTR_VARIANT
            : Consequence.FIVE_PRIME_UTR_VARIANT);
  }

  private CodonVariant getReferenceSequenceCodon(int pos, byte[] alt, Strand strand, Cds.Part cds) {
    int codonPos;
    char[] refSequence =
        switch (strand) {
          case Strand.POSITIVE -> {
            codonPos = (pos - cds.getStart() - cds.getPhase()) % 3;
            yield annotationDb.getSequence(pos - codonPos, pos - codonPos + 2, strand);
          }
          case Strand.NEGATIVE -> {
            codonPos = (cds.getStop() - pos - cds.getPhase()) % 3;
            yield annotationDb.getSequence(pos + codonPos, pos + codonPos - 2, strand);
          }
          default -> throw new RuntimeException();
        };

    if (refSequence == null) throw new RuntimeException();

    char[] altSequence = new char[3];
    System.arraycopy(refSequence, 0, altSequence, 0, 3);

    char altNuc =
        switch (strand) {
          case POSITIVE -> (char) alt[0];
          case NEGATIVE ->
              switch ((char) alt[0]) {
                case 'A' -> 'T';
                case 'C' -> 'G';
                case 'G' -> 'C';
                case 'T' -> 'A';
                case 'N' -> 'N';
                default -> throw new IllegalStateException("Unexpected value: " + (char) alt[0]);
              };
        };
    altSequence[codonPos] = altNuc;

    return new CodonVariant(from(refSequence), from(altSequence));
  }

  private record CodonVariant(Codon ref, Codon alt) {}
}
