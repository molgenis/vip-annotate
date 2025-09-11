package org.molgenis.vipannotate.format.vcf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record VcfMetaInfo(List<Line> lines) {
  public interface Line {
    String key();

    String line();
  }

  public record Structured(String key, Map<String, String> keyValues) implements Line {
    public Structured {
      if (key.isEmpty()) {
        throw new IllegalArgumentException("key must not be empty");
      }
      String id = keyValues.get("ID");
      if (id == null) {
        throw new IllegalArgumentException("structured vcf header line is missing required ID key");
      }
      if (id.isEmpty()) {
        throw new IllegalArgumentException("structured vcf header line ID value must not be empty");
      }
    }

    public String id() {
      return Objects.requireNonNull(keyValues.get("ID"));
    }

    @Override
    public String line() {
      StringBuilder builder = new StringBuilder();
      builder.append("##");
      builder.append(key);
      builder.append('=');
      builder.append('<');

      keyValues.forEach((key, value) -> builder.append(key).append("=").append(value).append(','));
      if (!keyValues.isEmpty()) {
        builder.deleteCharAt(builder.length() - 1);
      }

      builder.append('>');
      return builder.toString();
    }
  }

  /**
   * An unstructured meta-information line: ##key=value
   *
   * @param key denoting the type of meta-information recorded
   * @param value may not be empty and must not start with a ‘<’ character
   * @see <a href="https://samtools.github.io/hts-specs/VCFv4.5.pdf">VCFv4.5 specification</a>
   */
  public record Unstructured(String key, String value) implements Line {
    public Unstructured {
      if (value.isEmpty()) {
        throw new IllegalArgumentException("value may not be empty");
      }
      if (value.charAt(0) == '<') {
        throw new IllegalArgumentException("value must must not start with a ‘<’ character");
      }
    }

    @Override
    public String line() {
      return key + "=" + value;
    }
  }

  public void addOrUpdateInfo(
          String id, String number, String type, String description, @Nullable String source, @Nullable String version) {
    LinkedHashMap<String, String> keyValues = new LinkedHashMap<>();
    keyValues.put("ID", id);
    keyValues.put("NUMBER", number);
    keyValues.put("TYPE", type);
    keyValues.put("DESCRIPTION", '\"' + description + '\"');
    if (source != null) {
      keyValues.put("SOURCE", '\"' + source + '\"');
    }
    if (version != null) {
      keyValues.put("VERSION", '\"' + version + '\"');
    }
    Structured structured = new Structured("INFO", keyValues);

    int existingIndex = -1;
    for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
      Line aLine = lines.get(i);
      if (aLine instanceof Structured aStructuredLine) {
        if (aStructuredLine.key().equals("INFO") && aStructuredLine.id().equals(id)) {
          existingIndex = i;
          break;
        }
      }
    }

    if (existingIndex != -1) {
      lines.add(existingIndex, structured);
    } else {
      lines.add(structured);
    }
  }
}
