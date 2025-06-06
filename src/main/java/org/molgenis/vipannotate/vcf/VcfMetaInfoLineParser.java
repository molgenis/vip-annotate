package org.molgenis.vipannotate.vcf;

import java.util.LinkedHashMap;
import lombok.NonNull;

public class VcfMetaInfoLineParser {

  public VcfMetaInfo.Line parseLine(@NonNull String line) {
    if (!line.startsWith("##")) {
      throw new VcfMetaInfoParserException(line, "line must start with '##'");
    }

    int index = line.indexOf('=');
    if (index == -1) {
      throw new VcfMetaInfoParserException(line, "line does not match format '##key=value'");
    }

    String key = line.substring(0, index);
    if (key.isEmpty()) {
      throw new VcfMetaInfoParserException(
          line, "line does not format '##key=value', key must not be empty");
    }

    String value = index + 1 < line.length() ? line.substring(index + 1) : "";
    if (value.isEmpty()) {
      throw new VcfMetaInfoParserException(
          line, "line does not format '##key=value', value must not be empty");
    }

    VcfMetaInfo.Line vcfHeaderLine;
    if (value.charAt(0) == '<' && value.charAt(value.length() - 1) == '>') {
      vcfHeaderLine = parseLineStructured(key, value);
    } else {
      vcfHeaderLine = parseLineUnstructured(key, value);
    }
    return vcfHeaderLine;
  }

  private VcfMetaInfo.Structured parseLineStructured(String key, String value) {
    LinkedHashMap<String, String> keyValues = new LinkedHashMap<>();

    int currentIndex = 1;
    int index;
    while ((index = value.indexOf('=', currentIndex)) != -1) {
      String keyValueKey = value.substring(currentIndex, index);
      currentIndex = index + 1;

      String keyValueValue;
      if (value.charAt(currentIndex) == '\"') {
        int keyValueValueEndIndex = value.indexOf('\"', currentIndex + 1);
        keyValueValue = value.substring(currentIndex, keyValueValueEndIndex + 1);
        currentIndex = keyValueValueEndIndex + 1;
      } else {
        int keyValueValueEndIndex = value.indexOf(',', currentIndex);
        if (keyValueValueEndIndex == -1) {
          keyValueValue = value.substring(currentIndex, value.length() - 1);
          currentIndex = value.length() - 1 + 1;
        } else {
          keyValueValue = value.substring(currentIndex, keyValueValueEndIndex);
          currentIndex = keyValueValueEndIndex + 1;
        }
      }
      keyValues.put(keyValueKey, keyValueValue);
    }

    return new VcfMetaInfo.Structured(key.substring(2), keyValues);
  }

  private VcfMetaInfo.Unstructured parseLineUnstructured(String key, String value) {
    return new VcfMetaInfo.Unstructured(key, value);
  }
}
