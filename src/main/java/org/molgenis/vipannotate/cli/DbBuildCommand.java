package org.molgenis.vipannotate.cli;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.molgenis.vipannotate.annotation.AnnotationDbBuilder;
import org.molgenis.vipannotate.annotation.AnnotationSpecReader;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpec;
import org.molgenis.vipannotate.format.vdb.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Logger;

public class DbBuildCommand implements Command {
  @Override
  public void run(String[] args) {
    DbBuildArgs dbBuildArgs = new DbBuildArgsParser().parse(args);
    Path inputRecipe = dbBuildArgs.inputRecipe();

    // construct output db path
    String dbFileName = inputRecipe.getFileName().toString().replaceFirst("\\.json$", ".vdb");
    Path outputDir = dbBuildArgs.outputDir();
    if (outputDir == null) {
      outputDir = Paths.get(System.getProperty("user.dir"));
    }
    Path outputDb = outputDir.resolve(dbFileName);

    // build db
    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    buildDb(inputRecipe, outputDb, dbBuildArgs.force() != null && dbBuildArgs.force());

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }

  private static void buildDb(Path inputRecipe, Path outputDb, boolean force) {

    byte[] bytes;
    try {
      bytes = Files.readAllBytes(inputRecipe);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    try (MemoryBuffer memBuffer =
        MemoryBuffer.wrap(new byte[bytes.length + MemoryBuffer.VAR_INT_MAX_BYTE_SIZE])) {
      memBuffer.putByteArray(bytes);
      memBuffer.flip();
      AnnotationSpec annotationSpec = AnnotationSpecReader.create().readSpec(memBuffer);

      VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
      VdbArchiveWriter vdbArchiveWriter =
          VdbArchiveWriterFactory.create(memBufferFactory).create(outputDb, force);
      try (PartitionedVdbArchiveWriter archiveWriter =
          PartitionedVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
        archiveWriter.write("spec", Compression.ZSTD, IoMode.BUFFERED, memBuffer);
        new AnnotationDbBuilder()
            .create(annotationSpec, requireNonNull(inputRecipe.getParent()), archiveWriter);
      }
    }
  }
}
