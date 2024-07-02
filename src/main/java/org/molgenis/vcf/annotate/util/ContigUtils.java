package org.molgenis.vcf.annotate.util;

import org.molgenis.vcf.annotate.db.model.Chromosome;

public class ContigUtils {
  private ContigUtils() {}

  public static Chromosome map(String contig) {
    return switch (contig) {
      case "chr1" -> Chromosome.CHR1;
      case "chr2" -> Chromosome.CHR2;
      case "chr3" -> Chromosome.CHR3;
      case "chr4" -> Chromosome.CHR4;
      case "chr5" -> Chromosome.CHR5;
      case "chr6" -> Chromosome.CHR6;
      case "chr7" -> Chromosome.CHR7;
      case "chr8" -> Chromosome.CHR8;
      case "chr9" -> Chromosome.CHR9;
      case "chr10" -> Chromosome.CHR10;
      case "chr11" -> Chromosome.CHR11;
      case "chr12" -> Chromosome.CHR12;
      case "chr13" -> Chromosome.CHR13;
      case "chr14" -> Chromosome.CHR14;
      case "chr15" -> Chromosome.CHR15;
      case "chr16" -> Chromosome.CHR16;
      case "chr17" -> Chromosome.CHR17;
      case "chr18" -> Chromosome.CHR18;
      case "chr19" -> Chromosome.CHR19;
      case "chr20" -> Chromosome.CHR20;
      case "chr21" -> Chromosome.CHR21;
      case "chr22" -> Chromosome.CHR22;
      case "chrX" -> Chromosome.CHRX;
      case "chrY" -> Chromosome.CHRY;
      case "chrM" -> Chromosome.CHRM;
      default -> throw new IllegalStateException("Unexpected value: " + contig);
    };
  }
}
