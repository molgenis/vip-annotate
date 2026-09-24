package org.molgenis.vipannotate.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.spec.AnnotationDbSpec;
import org.molgenis.vipannotate.annotation.spec.AnnotationDbSpecReader;
import org.molgenis.vipannotate.format.vdb.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Logger;
import tools.jackson.databind.DatabindException;

public class DbBuildCommand implements Command {
  @Override
  public void run(String[] args) {
    DbBuildArgs dbBuildArgs = new DbBuildArgsParser().parse(args);
    Path input = dbBuildArgs.input();
    Path inputDef = dbBuildArgs.inputDef();

    // construct output db path
    String dbFileName = inputDef.getFileName().toString().replaceFirst("\\.json$", ".vdb");
    Path outputDir = dbBuildArgs.outputDir();
    if (outputDir == null) {
      outputDir = Paths.get(System.getProperty("user.dir"));
    }
    Path outputDb = outputDir.resolve(dbFileName);

    // build db
    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    buildDb(input, inputDef, outputDb, dbBuildArgs.force() != null && dbBuildArgs.force());

    long endCreateDb = System.currentTimeMillis();
    Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
  }

  private static void buildDb(Path input, Path inputDef, Path outputDb, boolean force) {
    byte[] bytes;
    try {
      bytes = Files.readAllBytes(inputDef);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    try (MemoryBuffer memBuffer =
        MemoryBuffer.wrap(new byte[bytes.length + MemoryBuffer.VAR_INT_MAX_BYTE_SIZE])) {
      memBuffer.putByteArray(bytes);
      memBuffer.flip();

      AnnotationDbSpec annotationDbSpec;
      try {
        annotationDbSpec = AnnotationDbSpecReader.create().readSpec(memBuffer);
      } catch (DatabindException e) {
        throw new IllegalStateException("error parsing %s".formatted(inputDef), e);
      }

      VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
      VdbArchiveWriter vdbArchiveWriter =
          VdbArchiveWriterFactory.create(memBufferFactory).create(outputDb, force);
      try (PartitionedVdbArchiveWriter archiveWriter =
          PartitionedVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
        AnnotationDbBuilder.create().buildDb(input, annotationDbSpec, archiveWriter);
      } catch (Throwable t) {
        try {
          Files.deleteIfExists(outputDb);
        } catch (IOException _) {
          // ignore
        }
        throw t;
      }
    }
  }
}
