package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FathmmMklParser {
  public FathmmMklTsvRecord parse(String[] tokens) {
    if (tokens.length != 5) {
      throw new IllegalArgumentException("Fathmm MKL parser expects 5 tokens");
    }

    int i = 0;
    String chrom = tokens[i++];
    int pos = Integer.parseInt(tokens[i++]);
    String ref = tokens[i++];
    String alt = tokens[i++];
    double score = parseDouble(tokens[i]);

    return new FathmmMklTsvRecord(chrom, pos, ref, alt, score);
  }

  private static double parseDouble(String token) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Fathmm MKL parser expects a non-null non-empty token");
    }
    return Double.parseDouble(token);
  }
}
