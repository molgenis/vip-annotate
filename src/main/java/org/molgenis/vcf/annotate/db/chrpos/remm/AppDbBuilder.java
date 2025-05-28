package org.molgenis.vcf.annotate.db.chrpos.remm;

import java.io.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.util.Logger;

public class AppDbBuilder {
  // FIXME proper CLI with arg validation etc.
  public static void main(String[] args) {
    File remmFile = new File(args[0]);
    File outputFile = new File(args[1]);

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    try (ZipArchiveOutputStream zipArchiveOutputStream = createWriter(outputFile)) {
      new RemmAnnotationDbBuilder().create(remmFile, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }

  private static ZipArchiveOutputStream createWriter(File zipFile) throws FileNotFoundException {
    return new ZipArchiveOutputStream(
        new BufferedOutputStream(new FileOutputStream(zipFile), 1048576));
  }
}
