package org.molgenis.vipannotate.db.chrpos.remm;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class RemmIterator implements Iterator<RemmIterator.RemmFeature> {
  /**
   * @param chr chromosome
   * @param start start position (1-based)
   * @param score Regulatory Mendelian Mutation (ReMM) score
   */
  public record RemmFeature(String chr, int start, double score) {}

  private final BufferedReader bufferedReader;
  private String line;

  public RemmIterator(BufferedReader bufferedReader) {
    this.bufferedReader = requireNonNull(bufferedReader);
  }

  @Override
  public boolean hasNext() {
    try {
      do {
        line = bufferedReader.readLine();
      } while (line != null && line.startsWith("#"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return line != null;
  }

  @Override
  public RemmFeature next() {
    String[] tokens = line.split("\t", -1);
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    double score = Double.parseDouble(tokens[2]);
    return new RemmFeature(chr, start, score);
  }
}
