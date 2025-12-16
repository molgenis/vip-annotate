package org.molgenis.vipannotate.annotation.phylop;

public class PhyloPParser {
  public PhyloPBedFeature parse(String[] tokens) {
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    int end = Integer.parseInt(tokens[2]);
    double score = Double.parseDouble(tokens[3]);

    return new PhyloPBedFeature(chr, start, end, score);
  }
}
