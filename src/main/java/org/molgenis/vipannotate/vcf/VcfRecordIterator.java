package org.molgenis.vipannotate.vcf;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

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
    return parse(currentLine);
  }

  @Override
  public void close() {
    try {
      reader.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private VcfRecord parse(String line) {
    String[] tokens = line.split("\t", -1);

    String chrom = tokens[0];
    long pos = Long.parseLong(tokens[1]);
    String[] id = parseId(tokens[2]);
    String ref = tokens[3];
    String[] alt = parseAlt(tokens[4]);
    String qual = parseQual(tokens[5]);
    String[] filter = parseFilter(tokens[6]);
    Map<String, String> info = parseInfo(tokens[7]);

    String[] format;
    String[] sampleData;
    boolean hasGenotypeInfo = tokens.length > 8;
    if (hasGenotypeInfo) {
      format = parseFormat(tokens[8]);
      sampleData = Arrays.copyOfRange(tokens, 9, tokens.length);
    } else {
      format = null;
      sampleData = null;
    }

    return new VcfRecord(chrom, pos, id, ref, alt, qual, filter, info, format, sampleData);
  }

  private static String[] parseFormat(String token) {
    String[] format;
    if (token.equals(".")) {
      format = new String[0];
    } else {
      format = token.split(":", -1);
    }
    return format;
  }

  private static String[] parseId(String token) {
    String[] id;
    if (token.equals(".")) {
      id = new String[0];
    } else {
      id = token.split(",", -1);
    }
    return id;
  }

  private static String[] parseAlt(String token) {
    String[] alt;
    if (token.equals(".")) {
      alt = new String[0];
    } else {
      alt = token.split(",", -1);
      for (int i = 0, altLength = alt.length; i < altLength; i++) {
        String altToken = alt[i];
        if (altToken.equals(".")) {
          alt[i] = null;
        }
      }
    }
    return alt;
  }

  private static String parseQual(String token) {
    String qual;
    if (token.equals(".")) {
      qual = null;
    } else {
      qual = token;
    }
    return qual;
  }

  private static String[] parseFilter(String token) {
    String[] filter;
    if (token.equals(".")) {
      filter = new String[0];
    } else {
      filter = token.split(";", -1);
    }
    return filter;
  }

  private Map<String, String> parseInfo(String token) {
    if (token.equals(".")) {
      return new LinkedHashMap<>(0);
    }
    String[] infoTokens = token.split(";", -1);

    int expectedSize = infoTokens.length;
    int capacity = (int) Math.ceil(expectedSize / 0.75);
    Map<String, String> info = new LinkedHashMap<>(capacity);

    for (String infoToken : infoTokens) {
      String[] infoTokenKeyValue = infoToken.split("=", -1);
      info.put(
          infoTokenKeyValue[0],
          infoTokenKeyValue.length != 1 ? infoTokenKeyValue[1] : null); // null for flag
    }
    return info;
  }
}
