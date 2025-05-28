package org.molgenis.vcf.annotate.vcf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public record VcfHeader(List<String> lines) {
  public void addLine(String line) {
    lines.add(lines.size() - 1, line);
  }

  public static VcfHeader create(BufferedReader bufferedReader) {
    List<String> lines = new ArrayList<>();

    String line;
    do {
      try {
        line = bufferedReader.readLine();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      lines.add(line);
    } while (line.startsWith("##"));

    return new VcfHeader(lines);
  }
}
