package org.molgenis.vcf.annotate.db.chrpos.remm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.chrpos.ZipCompressionContext;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(File phyloPFile, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContext zipCompressionContext = new ZipCompressionContext();
    try (BufferedReader reader = createReader(phyloPFile)) {
      new RemmAnnotationDbWriter()
          .create(new RemmIterator(reader), zipCompressionContext, zipOutputStream);
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
