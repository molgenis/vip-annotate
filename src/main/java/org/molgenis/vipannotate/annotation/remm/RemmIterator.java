package org.molgenis.vipannotate.annotation.remm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemmIterator implements Iterator<RemmTsvRecord> {
  @NonNull private final BufferedReader bufferedReader;
  private String line;

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
  public RemmTsvRecord next() {
    String[] tokens = line.split("\t", -1);
    String chr = tokens[0];
    int start = Integer.parseInt(tokens[1]);
    double score = Double.parseDouble(tokens[2]);
    return new RemmTsvRecord(chr, start, score);
  }
}
