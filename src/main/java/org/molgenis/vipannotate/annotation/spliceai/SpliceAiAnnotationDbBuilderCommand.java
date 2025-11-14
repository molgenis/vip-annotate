package org.molgenis.vipannotate.annotation.spliceai;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.RegionParser;
import org.molgenis.vipannotate.annotation.AnnotationVdbArchiveWriter;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriter;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriterFactory;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.util.HgncToNcbiGeneIdMapper;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

// TODO dedup with *AnnotationDbBuilderCommand
public class SpliceAiAnnotationDbBuilderCommand implements Command {
  @Override
  public void run(String[] args) {
    SpliceAiCommandArgs commandArgs = new SpliceAiCommandArgsParser().parse(args);

    Input spliceAiInput = commandArgs.input();
    Path ncbiGeneFile = commandArgs.ncbiGeneFile();
    Path faiFile = commandArgs.faiFile();
    Path dbOutput = commandArgs.output();

    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    ContigRegistry contigRegistry = ContigRegistry.create(FastaIndexParser.create(faiFile));
    String regionsStr = commandArgs.regionsStr();
    List<Region> regions =
        regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;
    HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper = HgncToNcbiGeneIdMapper.create(ncbiGeneFile);

    boolean force = commandArgs.force() != null && commandArgs.force();
    VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
    VdbArchiveWriter vdbArchiveWriter =
        VdbArchiveWriterFactory.create(memBufferFactory).create(dbOutput, force);
    try (AnnotationVdbArchiveWriter archiveWriter =
        AnnotationVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
      new SpliceAiAnnotationDbBuilder()
          .create(
              spliceAiInput,
              hgncToNcbiGeneIdMapper,
              regions,
              contigRegistry,
              archiveWriter,
              memBufferFactory);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
