package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.fasta.FastaIndex;

@RequiredArgsConstructor
public class FathmmMklParser {
  private final FastaIndex fastaIndex;

  public FathmmMklTsvRecord parse(String[] tokens) {
    if (tokens.length != 5) {
      throw new IllegalArgumentException("Fathmm MKL parser expects 5 tokens");
    }

    int i = 0;
    String chrom = parseChrom(tokens[i++]);
    int pos = Integer.parseInt(tokens[i++]);
    String ref = tokens[i++];
    String alt = tokens[i++];
    double score = parseDouble(tokens[i]);

    return new FathmmMklTsvRecord(chrom, pos, ref, alt, score);
  }

  private String parseChrom(String token) {
    if (!fastaIndex.containsReferenceSequence(token)) {
      throw new IllegalArgumentException(token + " is not a valid chrom");
    }
    return token;
  }

  private static double parseDouble(String token) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Fathmm MKL parser expects a non-null non-empty token");
    }
    return Double.parseDouble(token);
  }
}
