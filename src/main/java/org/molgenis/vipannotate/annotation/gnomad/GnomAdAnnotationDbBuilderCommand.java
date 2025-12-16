package org.molgenis.vipannotate.annotation.gnomad;

import java.nio.file.Path;
import java.util.List;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.annotation.Region;
import org.molgenis.vipannotate.cli.Command;
import org.molgenis.vipannotate.cli.DbBuildSubCommandArgs;
import org.molgenis.vipannotate.cli.DbBuildSubCommandArgsParser;
import org.molgenis.vipannotate.cli.RegionParser;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveWriter;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriter;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriterFactory;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

// TODO dedup with *AnnotationDbBuilderCommand
public class GnomAdAnnotationDbBuilderCommand implements Command {
  private static final String COMMAND = "gnomad";

  @Override
  public void run(String[] args) {
    DbBuildSubCommandArgs commandArgs = new DbBuildSubCommandArgsParser(COMMAND).parse(args);

    Input gnomAdInput = commandArgs.input();
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
    try (PartitionedVdbArchiveWriter archiveWriter =
        PartitionedVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
      new GnomAdAnnotationDbBuilder()
          .create(gnomAdInput, regions, fastaIndex, archiveWriter, memBufferFactory);
    }

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }
}
