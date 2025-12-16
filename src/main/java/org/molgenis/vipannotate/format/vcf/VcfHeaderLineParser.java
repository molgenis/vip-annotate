package org.molgenis.vipannotate.format.vcf;

public class VcfHeaderLineParser {
  private static final String HEADER_LINE_PREFIX = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO";

  public VcfHeaderLine parseLine(String line) {
    if (!line.startsWith(HEADER_LINE_PREFIX)) {
      throw new VcfParserException(
          "invalid vcf header line '%s': line must start with '%s'"
              .formatted(line, HEADER_LINE_PREFIX));
    }
    return new VcfHeaderLine(line);
  }
}
