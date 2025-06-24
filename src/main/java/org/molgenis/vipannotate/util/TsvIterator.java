package org.molgenis.vipannotate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jspecify.annotations.Nullable;

/**
 * Basic tab-separated value iterator that skips lines starting with '#' as well as empty lines.
 * Does not support quoted values and does not support escaped tab character.
 */
public class TsvIterator implements Iterator<String[]> {
  private final BufferedReader bufferedReader;
  @Nullable
  private String nextLine;

  public TsvIterator(BufferedReader bufferedReader) {
    this.bufferedReader = bufferedReader;
    advance();
  }

  @Override
  public boolean hasNext() {
    return nextLine != null;
  }

  @Override
  public String[] next() {
    if (nextLine == null) {
      throw new NoSuchElementException();
    }
    String line = nextLine;
    advance();
    return line.split("\t", -1);
  }

  private void advance() {
    nextLine = null;
    String line;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        if (!line.startsWith("#") && !line.isEmpty()) {
          nextLine = line;
          break;
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
