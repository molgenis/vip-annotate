package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.annotation.DbCommandArgs;
import org.molgenis.vipannotate.annotation.DbCommandArgsParser;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Region;
import org.molgenis.vipannotate.util.RegionParser;

public class NcERAnnotationDbBuilderCommand implements Command {
  private static final String COMMAND = "ncer";

  @Override
  public void run(String[] args) {
    DbCommandArgs commandArgs = new DbCommandArgsParser(COMMAND).parse(args);

    Path ncERFile = commandArgs.inputFile();
    Path faiFile = commandArgs.faiFile();
    Path outputFile = commandArgs.outputFile();

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);
    ContigRegistry contigRegistry = ContigRegistry.create(fastaIndex);
    String regionsStr = commandArgs.regionsStr();
    List<Region> regions =
        regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(outputFile)) {
      new NcERAnnotationDbBuilder().create(ncERFile, regions, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
