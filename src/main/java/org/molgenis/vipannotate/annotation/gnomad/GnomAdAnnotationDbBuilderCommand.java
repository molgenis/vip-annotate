package org.molgenis.vipannotate.annotation.gnomad;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.Logger;

public class GnomAdAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    GnomAdCommandArgs gnomAdCommandArgs = new GnomAdCommandArgsParser().parse(args);

    Path gnomAdFile = gnomAdCommandArgs.inputFile();
    Path faiFile = gnomAdCommandArgs.faiFile();
    Path outputFile = gnomAdCommandArgs.outputFile();

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(outputFile)) {
      new GnomAdAnnotationDbBuilder().create(gnomAdFile, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
