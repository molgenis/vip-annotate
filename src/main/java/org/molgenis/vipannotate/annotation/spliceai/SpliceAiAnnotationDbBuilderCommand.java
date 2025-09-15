package org.molgenis.vipannotate.annotation.spliceai;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.HgncToNcbiGeneIdMapper;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Region;
import org.molgenis.vipannotate.util.RegionParser;

public class SpliceAiAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    SpliceAiCommandArgs spliceAiCommandArgs = new SpliceAiCommandArgsParser().parse(args);
    if (spliceAiCommandArgs.debugMode() != null && spliceAiCommandArgs.debugMode()) {
      Logger.ENABLE_DEBUG_LOGGING = true;
    }

    Path spliceAiFile1 = spliceAiCommandArgs.inputFile1();
    Path spliceAiFile2 = spliceAiCommandArgs.inputFile2();
    Path ncbiGeneFile = spliceAiCommandArgs.ncbiGeneFile();
    Path faiFile = spliceAiCommandArgs.faiFile();
    Path outputFile = spliceAiCommandArgs.outputFile();

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    ContigRegistry contigRegistry = ContigRegistry.create(FastaIndexParser.create(faiFile));
    String regionsStr = spliceAiCommandArgs.regionsStr();
    List<Region> regions =
        regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;
    HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper = HgncToNcbiGeneIdMapper.create(ncbiGeneFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(outputFile)) {
      new SpliceAiAnnotationDbBuilder()
          .create(
              spliceAiFile1,
              spliceAiFile2,
              hgncToNcbiGeneIdMapper,
              regions,
              contigRegistry,
              zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
