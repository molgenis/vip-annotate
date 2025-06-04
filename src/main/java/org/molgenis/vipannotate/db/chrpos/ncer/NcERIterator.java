package org.molgenis.vipannotate.db.chrpos.ncer;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class NcERIterator implements Iterator<NcERIterator.NcERFeature> {
  /**
   * @param chr chromosome
   * @param start start position (0-based, inclusive)
   * @param end end position (0-based, exclusive)
   * @param perc genome-wide ncER percentile. The higher the percentile, the more likely essential
   *     (in terms of regulation) the region is.
   */
  public record NcERFeature(String chr, int start, int end, double perc) {}

  private final BufferedReader bufferedReader;
  private String line;

  public NcERIterator(BufferedReader bufferedReader) {
    this.bufferedReader = requireNonNull(bufferedReader);
  }

  @Override
  public boolean hasNext() {
    try {
      line = bufferedReader.readLine();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return line != null;
  }

  @Override
  public NcERFeature next() {
    String[] tokens = line.split("\t", -1);
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    int end = Integer.parseInt(tokens[2]);
    double perc = Double.parseDouble(tokens[3]);
    return new NcERFeature(chr, start, end, perc);
  }
}
