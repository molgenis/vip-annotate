package org.molgenis.vcf.annotate.db.chrpos;

import java.io.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.chrpos.ncer.NcERAnnotationDbBuilder;
import org.molgenis.vcf.annotate.db.chrpos.phylop.PhyloPAnnotationDbBuilder;
import org.molgenis.vcf.annotate.db.chrpos.remm.RemmAnnotationDbBuilder;
import org.molgenis.vcf.annotate.util.Logger;

public class AppDbBuilder {
  // FIXME proper CLI with arg validation etc.
  public static void main(String[] args) {
    File ncERFile = new File(args[0]);
    File phylopFile = new File(args[1]);
    File remmFile = new File(args[2]);
    File outputFile = new File(args[3]);

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    try (ZipArchiveOutputStream zipArchiveOutputStream = createWriter(outputFile)) {
      new NcERAnnotationDbBuilder().create(ncERFile, zipArchiveOutputStream);
      new PhyloPAnnotationDbBuilder().create(phylopFile, zipArchiveOutputStream);
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
