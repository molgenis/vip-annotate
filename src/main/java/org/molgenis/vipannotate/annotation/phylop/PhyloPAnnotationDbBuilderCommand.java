package org.molgenis.vipannotate.annotation.phylop;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.Logger;

public class PhyloPAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    PhylopCommandArgs phylopCommandArgs = new PhylopCommandArgsParser().parse(args);

    Path phyloPFile = phylopCommandArgs.inputFile();
    Path faiFile = phylopCommandArgs.faiFile();
    Path outputFile = phylopCommandArgs.outputFile();

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
