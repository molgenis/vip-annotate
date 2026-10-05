package org.molgenis.vipannotate.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.def.AnnotationDbDef;
import org.molgenis.vipannotate.annotation.def.AnnotationDbDefReader;
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

    Path output = dbBuildArgs.output();
    if (output == null) {
      output = createOutput(input);
    }

    // build db
    Logger.debug("creating database ...");
    long startCreateDb = System.currentTimeMillis();

    buildDb(input, inputDef, output, dbBuildArgs.force() != null && dbBuildArgs.force());

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

      AnnotationDbDef annotationDbDef;
      try {
        annotationDbDef = AnnotationDbDefReader.create().readFrom(memBuffer);
      } catch (DatabindException e) {
        throw new IllegalStateException("error parsing %s".formatted(inputDef), e);
      }

      VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
      VdbArchiveWriter vdbArchiveWriter =
          VdbArchiveWriterFactory.create(memBufferFactory).create(outputDb, force);
      try (PartitionedVdbArchiveWriter archiveWriter =
          PartitionedVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
        AnnotationDbBuilder.create().buildDb(input, annotationDbDef, archiveWriter);
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

  private static Path createOutput(Path inputPath) {
    String filename = inputPath.getFileName().toString();

    for (String extension : List.of(".bgz", ".gz")) {
      if (filename.endsWith(extension)) {
        filename = filename.substring(0, filename.length() - extension.length());
        break;
      }
    }

    for (String extension : List.of(".bed", ".tsv", ".vcf")) {
      if (filename.endsWith(extension)) {
        filename = filename.substring(0, filename.length() - extension.length());
        break;
      }
    }

    return Path.of(filename + ".vdb");
  }
}
