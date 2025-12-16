package org.molgenis.vipannotate.format.vcf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

public record VcfHeaderLine(String line) {
  public void write(Writer writer) {
    try {
      writer.write(line);
      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
