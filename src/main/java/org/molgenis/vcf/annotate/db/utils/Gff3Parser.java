package org.molgenis.vcf.annotate.db.utils;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import org.molgenis.vcf.annotate.db.model.Strand;

public class Gff3Parser implements AutoCloseable {
  private final BufferedReader reader;

  public Gff3Parser(Reader reader) {
    this.reader =
        requireNonNull(reader) instanceof BufferedReader
            ? (BufferedReader) reader
            : new BufferedReader(reader);
  }

  public Feature parseLine() throws IOException {
    String line = reader.readLine();
    if (line == null) return null;
    if (line.isEmpty() || line.charAt(0) == '#') return parseLine();

    String[] tokens = line.split("\t", -1);
    if (tokens.length != 9) throw new IOException();

    String seqid = tokens[0];
    String source = tokens[1];
    String type = tokens[2];
    long start = Long.parseLong(tokens[3]);
    long end = Long.parseLong(tokens[4]);
    String score = parseScore(tokens[5]);
    Strand strand = parseStrand(tokens[6]);
    Phase phase = parsePhase(tokens[7]);
    Attributes attributes = parseAttributes(tokens[8]);
    return new Feature(seqid, source, type, start, end, score, strand, phase, attributes);
  }

  private static String parseScore(String token) {
    return token.equals(".") ? null : token;
  }

  private static Strand parseStrand(String token) {
    if (token.length() != 1) throw new UncheckedIOException(new IOException());
    char c = token.charAt(0);
    return c != '.'
        ? switch (c) {
          case '+' -> Strand.PLUS;
          case '-' -> Strand.MINUS;
          case '?' -> Strand.UNKNOWN;
          default -> throw new IllegalStateException("Unexpected value: " + token.charAt(0));
        }
        : null;
  }

  private static Phase parsePhase(String token) {
    if (token.length() != 1) throw new UncheckedIOException(new IOException());
    char c = token.charAt(0);
    return c != '.'
        ? switch (c) {
          case '0' -> Phase.ZERO;
          case '1' -> Phase.ONE;
          case '2' -> Phase.TWO;
          default -> throw new IllegalStateException("Unexpected value: " + token.charAt(0));
        }
        : null;
  }

  private static Attributes parseAttributes(String token) {
    String[] tokens = token.split(";", -1);
    Map<String, Object> attributeMap = new HashMap<>();
    for (String keyValueToken : tokens) {
      String[] keyValueTokens = keyValueToken.split("=", -1);
      attributeMap.put(keyValueTokens[0], keyValueTokens[1]);
    }
    return new Attributes(attributeMap);
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public enum Phase {
    ZERO,
    ONE,
    TWO
  }

  /** Feature can be spread over multiple lines */
  public record Feature(
      String seqid,
      String source,
      String type,
      long start,
      long end,
      String score,
      Strand strand,
      Phase phase,
      Attributes attributes) {

    public String getAttributeId() {
      return getAttribute("ID");
    }

    public String getAttributeParent() {
      return getAttribute("Parent");
    }

    private String getAttribute(String attributeId) {
      return (String) attributes.attributeMap.get(attributeId);
    }
  }

  public record Attributes(Map<String, Object> attributeMap) {
    public Attributes {
      requireNonNull(attributeMap);
    }
  }
}
