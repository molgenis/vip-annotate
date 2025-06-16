package org.molgenis.vipannotate.annotation.ncer;


public class NcERParser {
  public NcERBedFeature parse(String[] tokens) {
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    int end = Integer.parseInt(tokens[2]);
    double perc = Double.parseDouble(tokens[3]);
    return new NcERBedFeature(chr, start, end, perc);
  }
}
