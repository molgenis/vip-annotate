package org.molgenis.vipannotate.db.gnomad.shortvariant.db;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.FastaIndexParser;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Zip;

public class AppDb {
  // FIXME proper CLI with arg validation etc.
  public static void main(String[] args) throws IOException {
    Path gnomAdFile = Path.of(args[1]);
    Path faiFile = Path.of(args[3]);
    Path outputFile = Path.of(args[5]);

    if (args.length == 7 && args[6].equals("--force")) {
      Files.deleteIfExists(outputFile);
    } else {
      if (Files.exists(outputFile)) {
        throw new IllegalArgumentException("Output file %s already exists".formatted(outputFile));
      }
    }

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(outputFile)) {
      new GnomAdShortVariantAnnotationDbBuilder()
          .create(gnomAdFile, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
