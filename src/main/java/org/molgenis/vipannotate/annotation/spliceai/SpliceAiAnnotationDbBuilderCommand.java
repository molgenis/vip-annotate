package org.molgenis.vipannotate.annotation.spliceai;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.RegionParser;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.HgncToNcbiGeneIdMapper;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;

// TODO dedup with *AnnotationDbBuilderCommand
public class SpliceAiAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    SpliceAiCommandArgs commandArgs = new SpliceAiCommandArgsParser().parse(args);

    Input spliceAiInput = commandArgs.input();
    Path ncbiGeneFile = commandArgs.ncbiGeneFile();
    Path faiFile = commandArgs.faiFile();
    Output dbOutput = commandArgs.output();

    if (dbOutput.path() == null) {
      // output db is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    ContigRegistry contigRegistry = ContigRegistry.create(FastaIndexParser.create(faiFile));
    String regionsStr = commandArgs.regionsStr();
    List<Region> regions =
        regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;
    HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper = HgncToNcbiGeneIdMapper.create(ncbiGeneFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(dbOutput)) {
      new SpliceAiAnnotationDbBuilder()
          .create(
              spliceAiInput,
              hgncToNcbiGeneIdMapper,
              regions,
              contigRegistry,
              zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
