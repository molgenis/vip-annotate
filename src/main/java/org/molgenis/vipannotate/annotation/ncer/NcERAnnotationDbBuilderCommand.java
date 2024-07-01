package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.AppDbCommandArgs;
import org.molgenis.vipannotate.AppDbCommandArgsParser;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.RegionParser;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;

// TODO dedup with *AnnotationDbBuilderCommand
public class NcERAnnotationDbBuilderCommand implements Command {
  private static final String COMMAND = "ncer";

  @Override
  public void run(String[] args) {
    AppDbCommandArgs commandArgs = new AppDbCommandArgsParser(COMMAND).parse(args);

    Input ncERInput = commandArgs.input();
    Path faiFile = commandArgs.faiFile();
    Output dbOutput = commandArgs.output();

    if (dbOutput.path() == null) {
      // output db is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);
    ContigRegistry contigRegistry = ContigRegistry.create(fastaIndex);
    String regionsStr = commandArgs.regionsStr();
    List<Region> regions =
        regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(dbOutput)) {
      new NcERAnnotationDbBuilder().create(ncERInput, regions, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
