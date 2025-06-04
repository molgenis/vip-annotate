package org.molgenis.vipannotate.vcf;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class VcfRecordIterator implements Iterator<VcfRecord>, AutoCloseable {
  private final BufferedReader reader;
  private String nextLine;

  public VcfRecordIterator(BufferedReader reader) {
    this.reader = requireNonNull(reader);
    advance();
  }

  private void advance() {
    try {
      nextLine = reader.readLine();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean hasNext() {
    return nextLine != null;
  }

  @Override
  public VcfRecord next() {
    if (nextLine == null) throw new NoSuchElementException();
    String currentLine = nextLine;
    advance();
    return new VcfRecord(currentLine.split("\t", -1));
  }

  @Override
  public void close() throws Exception {
    reader.close();
  }
}
