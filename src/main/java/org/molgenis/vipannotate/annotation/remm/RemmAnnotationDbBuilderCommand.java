package org.molgenis.vipannotate.annotation.remm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.FastaIndexParser;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.zip.Zip;

// FIXME proper CLI with arg validation etc.
public class RemmAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    Path remmFile = Path.of(args[1]);
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
      new RemmAnnotationDbBuilder().create(remmFile, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
