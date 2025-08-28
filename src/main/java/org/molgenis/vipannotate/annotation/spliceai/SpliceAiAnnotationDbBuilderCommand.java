package org.molgenis.vipannotate.annotation.spliceai;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.util.HgncToNcbiGeneIdMapper;
import org.molgenis.vipannotate.util.Logger;

public class SpliceAiAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    SpliceAiCommandArgs spliceAiCommandArgs = new SpliceAiCommandArgsParser().parse(args);

    Path spliceAiFile = spliceAiCommandArgs.inputFile();
    Path ncbiGeneFile = spliceAiCommandArgs.ncbiGeneFile();
    Path faiFile = spliceAiCommandArgs.faiFile();
    Path outputFile = spliceAiCommandArgs.outputFile();

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);
    HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper = HgncToNcbiGeneIdMapper.create(ncbiGeneFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream =
        Zip.createZipArchiveOutputStream(outputFile)) {
      new SpliceAiAnnotationDbBuilder()
          .create(spliceAiFile, hgncToNcbiGeneIdMapper, fastaIndex, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
