package org.molgenis.vcf.annotate.db.chrpos.ncer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.chrpos.ZipCompressionContext;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(File phyloPFile, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContext zipCompressionContext = new ZipCompressionContext();
    try (BufferedReader reader = createReader(phyloPFile)) {
      new NcERAnnotationDbWriter()
          .create(new NcERIterator(reader), zipCompressionContext, zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static BufferedReader createReader(File ncERFile) throws IOException {
    return new BufferedReader(
        new InputStreamReader(
            new GZIPInputStream(new FileInputStream(ncERFile)), StandardCharsets.UTF_8),
        1048576);
  }
}
