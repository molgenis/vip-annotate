package org.molgenis.vcf.annotate.db.chrpos.phylop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.util.FastaIndex;
import org.molgenis.vcf.annotate.util.FastaIndexParser;
import org.molgenis.vcf.annotate.util.Logger;
import org.molgenis.vcf.annotate.util.Zip;

public class AppDbBuilder {
  // FIXME proper CLI with arg validation etc.
  public static void main(String[] args) {
    Path phyloPFile = Path.of(args[1]);
    Path faiFile = Path.of(args[3]);
    Path outputFile = Path.of(args[5]);
    if (Files.exists(outputFile)) {
      throw new IllegalArgumentException("Output file %s already exists".formatted(outputFile));
    }
    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(outputFile)) {
      new PhyloPAnnotationDbBuilder().create(phyloPFile, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
