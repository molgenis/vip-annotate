package org.molgenis.vcf.annotate.db.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/** see <a href="https://www.ncbi.nlm.nih.gov/datasets/genome/GCF_000001405.40/">here</a> */
@Getter
public enum Chromosome {
  CHR1("NC_000001.11"),
  CHR2("NC_000002.12"),
  CHR3("NC_000003.12"),
  CHR4("NC_000004.12"),
  CHR5("NC_000005.10"),
  CHR6("NC_000006.12"),
  CHR7("NC_000007.14"),
  CHR8("NC_000008.11"),
  CHR9("NC_000009.12"),
  CHR10("NC_000010.11"),
  CHR11("NC_000011.10"),
  CHR12("NC_000012.12"),
  CHR13("NC_000013.11"),
  CHR14("NC_000014.9"),
  CHR15("NC_000015.10"),
  CHR16("NC_000016.10"),
  CHR17("NC_000017.11"),
  CHR18("NC_000018.10"),
  CHR19("NC_000019.10"),
  CHR20("NC_000020.11"),
  CHR21("NC_000021.9"),
  CHR22("NC_000022.11"),
  CHRX("NC_000023.11"),
  CHRY("NC_000024.10"),
  CHRM("NC_012920.1");

  private static final Map<String, Chromosome> idToChromosomeMap;

  static {
    idToChromosomeMap = HashMap.newHashMap(Chromosome.values().length);
    for (Chromosome chromosome : Chromosome.values()) {
      idToChromosomeMap.put(chromosome.getId(), chromosome);
    }
  }

  private final String id;

  Chromosome(String id) {
    this.id = requireNonNull(id);
  }

  public static Chromosome from(String id) {
    requireNonNull(id);

    Chromosome chromosome = idToChromosomeMap.get(id);
    if (chromosome == null) {
      throw new NullPointerException();
    }
    return chromosome;
  }
}
