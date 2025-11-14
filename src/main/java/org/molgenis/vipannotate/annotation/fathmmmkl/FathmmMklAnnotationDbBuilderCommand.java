package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vipannotate.AppDbCommandArgs;
import org.molgenis.vipannotate.AppDbCommandArgsParser;
import org.molgenis.vipannotate.Command;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.RegionParser;
import org.molgenis.vipannotate.annotation.AnnotationVdbArchiveWriter;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriter;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriterFactory;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

// TODO dedup with *AnnotationDbBuilderCommand
public class FathmmMklAnnotationDbBuilderCommand implements Command {
  private static final String COMMAND = "fathmm_mkl";

  @Override
  public void run(String[] args) {
    AppDbCommandArgs commandArgs = new AppDbCommandArgsParser(COMMAND).parse(args);

    Input input = commandArgs.input();
    Path faiFile = commandArgs.faiFile();
    Path dbOutput = commandArgs.output();

    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    FastaIndex fastaIndex = FastaIndexParser.create(faiFile);
    ContigRegistry contigRegistry = ContigRegistry.create(fastaIndex);
    String regionsStr = commandArgs.regionsStr();
    List<Region> regions =
        regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;

    boolean force = commandArgs.force() != null && commandArgs.force();

    VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
    VdbArchiveWriter vdbArchiveWriter =
        VdbArchiveWriterFactory.create(memBufferFactory).create(dbOutput, force);
    try (AnnotationVdbArchiveWriter archiveWriter =
        AnnotationVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
      new FathmmMklAnnotationDbBuilder()
          .create(input, regions, fastaIndex, archiveWriter, memBufferFactory);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
