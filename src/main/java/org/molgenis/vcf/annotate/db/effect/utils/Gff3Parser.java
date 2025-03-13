package org.molgenis.vcf.annotate.db.effect.utils;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * see <a
 * href="https://github.com/The-Sequence-Ontology/Specifications/blob/fe73505276dd324bf6a55773f3413fe2bed47af4/gff3.md">GFF3
 * specification</a>
 */
public class Gff3Parser implements AutoCloseable {
  private final BufferedReader reader;

  public Gff3Parser(Reader reader) {
    this.reader =
        requireNonNull(reader) instanceof BufferedReader
            ? (BufferedReader) reader
            : new BufferedReader(reader);
  }

  public Gff3 parse() throws IOException {
    return parse(null);
  }

  public Gff3 parse(Filter filter) throws IOException {
    Gff3 gff3 = new Gff3();
    Gff3Parser.Feature feature;
    while ((feature = parseLine(filter)) != null) {
      if (filter == null || include(feature, filter)) {
        gff3.addFeature(feature);
      }
    }
    return gff3;
  }

  private boolean include(Feature feature, Filter filter) {
    return filter.seqIds().contains(feature.seqId) && filter.sources().contains(feature.source);
  }

  public Feature parseLine(Filter filter) throws IOException {
    String line = reader.readLine();
    if (line == null) return null;
    if (line.isEmpty() || line.charAt(0) == '#') return parseLine(filter);

    String[] tokens = line.split("\t", -1);
    if (tokens.length != 9) throw new IOException();

    String seqId = decode(tokens[0]);
    String source = decode(tokens[1]);
    String type = decode(tokens[2]);
    long start = Long.parseLong(tokens[3]);
    long end = Long.parseLong(tokens[4]);
    String score = parseScore(tokens[5]);
    Strand strand = parseStrand(tokens[6]);
    Phase phase = parsePhase(tokens[7]);
    Attributes attributes = parseAttributes(tokens[8], filter);
    return new Feature(seqId, source, type, start, end, score, strand, phase, attributes);
  }

  private static String parseScore(String token) {
    return token.equals(".") ? null : token;
  }

  private static Strand parseStrand(String token) {
    if (token.length() != 1) throw new UncheckedIOException(new IOException());
    char c = token.charAt(0);
    return switch (c) {
      case '+' -> Strand.PLUS;
      case '-' -> Strand.MINUS;
      case '?' -> Strand.UNKNOWN;
      case '.' -> null;
      default -> throw new IllegalStateException("Unexpected value: " + c);
    };
  }

  private static Phase parsePhase(String token) {
    if (token.length() != 1) throw new UncheckedIOException(new IOException());
    char c = token.charAt(0);
    return switch (c) {
      case '0' -> Phase.ZERO;
      case '1' -> Phase.ONE;
      case '2' -> Phase.TWO;
      case '.' -> null;
      default -> throw new IllegalStateException("Unexpected value: " + c);
    };
  }

  private static Attributes parseAttributes(String token, Filter filter) {
    String[] tokens = token.split(";", -1);
    Map<String, Object> attributeMap = HashMap.newHashMap(tokens.length);
    for (String keyValueToken : tokens) {
      String[] keyValueTokens = keyValueToken.split("=", -1);
      String key = decode(keyValueTokens[0]);
      if (filter != null && filter.attributes.contains(key)) {
        Object value =
            switch (key) {
              case "Alias", "Dbxref", "Note", "Ontology_term", "Parent" -> {
                String[] valueTokens = keyValueTokens[1].split(",", -1);
                yield Arrays.stream(valueTokens)
                    .map(Gff3Parser::decode)
                    .collect(Collectors.toCollection(() -> new ArrayList<>(valueTokens.length)));
              }
              default -> decode(keyValueTokens[1]);
            };
        attributeMap.put(key, value);
      }
    }
    return new Attributes(attributeMap);
  }

  private static String decode(String encoded) {
    if (encoded.indexOf('%') == -1) return encoded;

    // FIXME decode control characters (%00 through %1F, %7F)
    return encoded
        .replace("%09", "\t")
        .replace("%0A", "\n")
        .replace("%0D", "\r")
        .replace("%25", "%")
        .replace("%26", "&")
        .replace("%2C", ",")
        .replace("%3B", ";")
        .replace("%3D", "=");
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public enum Strand implements Serializable {
    PLUS,
    MINUS,
    UNKNOWN;

    public static Strand from(String str) {
      return switch (str) {
        case "+" -> PLUS;
        case "-" -> MINUS;
        default -> throw new IllegalArgumentException("Unknown strand: " + str);
      };
    }
  }

  public enum Phase {
    ZERO,
    ONE,
    TWO
  }

  public record Feature(
      String seqId,
      String source,
      String type,
      long start,
      long end,
      String score,
      Strand strand,
      Phase phase,
      Attributes attributes) {

    public boolean hasAttribute(String attributeId) {
      return attributes().attributeMap().containsKey(attributeId);
    }

    public String getAttributeId() {
      return getAttribute("ID");
    }

    public List<String> getAttributeParent() {
      List<String> parents = getAttributeAsList("Parent");
      return parents != null ? parents : List.of();
    }

    public String getAttribute(String attributeId) {
      return (String) attributes.attributeMap.get(attributeId);
    }

    public List<String> getAttributeAsList(String attributeId) {
      //noinspection unchecked
      return (List<String>) attributes.attributeMap.get(attributeId);
    }
  }

  public record Attributes(Map<String, Object> attributeMap) {
    public Attributes {
      requireNonNull(attributeMap);
    }
  }

  public record Filter(Set<String> seqIds, Set<String> sources, Set<String> attributes) {}
}
