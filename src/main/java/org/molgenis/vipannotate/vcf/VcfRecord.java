package org.molgenis.vipannotate.vcf;

public record VcfRecord(String[] tokens) {
  public String getChrom() {
    return tokens[0];
  }

  // TODO improve performance (method likely called multiple)
  public int getPos() {
    return Integer.parseInt(tokens[1]);
  }

  public String getRef() {
    return tokens[3];
  }

  // TODO improve performance (method likely called multiple)
  public String[] getAlts() {
    return tokens[4].split(",", -1);
  }

  public String getInfo() {
    return tokens[5];
  }

  public void addInfo(String token) {
    tokens[5] = tokens[5].isEmpty() ? token : tokens[5] + ";" + token;
  }
}
