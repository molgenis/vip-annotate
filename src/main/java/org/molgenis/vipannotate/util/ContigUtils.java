package org.molgenis.vipannotate.util;

import org.molgenis.vipannotate.db.effect.model.FuryFactory;

public class ContigUtils {
  private ContigUtils() {}

  // FIXME extend with all GCA_000001405.15_GRCh38_no_alt_analysis_set contigs
  public static FuryFactory.Chromosome map(String contig) {
    FuryFactory.Chromosome chromosome = FuryFactory.Chromosome.from(contig);
    return chromosome != null
        ? chromosome
        : switch (contig) {
          case "chr1" -> FuryFactory.Chromosome.CHR1;
          case "chr2" -> FuryFactory.Chromosome.CHR2;
          case "chr3" -> FuryFactory.Chromosome.CHR3;
          case "chr4" -> FuryFactory.Chromosome.CHR4;
          case "chr5" -> FuryFactory.Chromosome.CHR5;
          case "chr6" -> FuryFactory.Chromosome.CHR6;
          case "chr7" -> FuryFactory.Chromosome.CHR7;
          case "chr8" -> FuryFactory.Chromosome.CHR8;
          case "chr9" -> FuryFactory.Chromosome.CHR9;
          case "chr10" -> FuryFactory.Chromosome.CHR10;
          case "chr11" -> FuryFactory.Chromosome.CHR11;
          case "chr12" -> FuryFactory.Chromosome.CHR12;
          case "chr13" -> FuryFactory.Chromosome.CHR13;
          case "chr14" -> FuryFactory.Chromosome.CHR14;
          case "chr15" -> FuryFactory.Chromosome.CHR15;
          case "chr16" -> FuryFactory.Chromosome.CHR16;
          case "chr17" -> FuryFactory.Chromosome.CHR17;
          case "chr18" -> FuryFactory.Chromosome.CHR18;
          case "chr19" -> FuryFactory.Chromosome.CHR19;
          case "chr20" -> FuryFactory.Chromosome.CHR20;
          case "chr21" -> FuryFactory.Chromosome.CHR21;
          case "chr22" -> FuryFactory.Chromosome.CHR22;
          case "chrX" -> FuryFactory.Chromosome.CHRX;
          case "chrY" -> FuryFactory.Chromosome.CHRY;
          case "chrM" -> FuryFactory.Chromosome.CHRM;
          default -> null; // FIXME never return null
        };
  }
}
