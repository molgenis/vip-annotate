package org.molgenis.vipannotate.annotation.ncer;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class NcERIterator implements Iterator<NcERBedFeature> {

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
  public NcERBedFeature next() {
    String[] tokens = line.split("\t", -1);
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    int end = Integer.parseInt(tokens[2]);
    double perc = Double.parseDouble(tokens[3]);
    return new NcERBedFeature(chr, start, end, perc);
  }
}
