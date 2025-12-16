package org.molgenis.vipannotate.format.vcf;

/** low memory, high performance, reusable, lazy parsing */
public final class Genotype extends Field {
  private Genotype(StringView fieldRaw) {
    super(fieldRaw);
  }

  public static Genotype wrap(String fieldRaw) {
    return Genotype.wrap(new StringView(fieldRaw));
  }

  public static Genotype wrap(StringView fieldRaw) {
    return new Genotype(fieldRaw);
  }

  @Override
  public String toString() {
    return "GENOTYPE=" + super.toString().replace('\t', ' ');
  }
}
