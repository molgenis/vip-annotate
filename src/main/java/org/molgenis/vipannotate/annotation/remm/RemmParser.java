package org.molgenis.vipannotate.annotation.remm;

public class RemmParser {
  public RemmTsvRecord parse(String[] tokens) {
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    double score = Double.parseDouble(tokens[2]);
    return new RemmTsvRecord(chr, start, score);
  }
}
