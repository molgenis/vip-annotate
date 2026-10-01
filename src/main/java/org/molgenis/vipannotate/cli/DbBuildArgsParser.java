package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.molgenis.vipannotate.util.Logger;

public class DbBuildArgsParser extends ArgsParser<DbBuildArgs> {
  @Override
  public DbBuildArgs parse(String[] args) {
    super.validate(args);

    Path inputDef = null;
    Path input = null;
    Path output = null;
    Boolean force = null;

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-d", "--definition" -> inputDef = Path.of(parseArgValue(args, i++, arg));
        case "-i", "--input" -> input = Path.of(parseArgValue(args, i++, arg));
        case "-o", "--output" -> output = Path.of(parseArgValue(args, i++, arg));
        case "-f", "--force" -> force = Boolean.TRUE;
        default -> throw new ArgValidationException("unknown option '%s'".formatted(arg));
      }
    }

    if (inputDef == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-d", "--definition"));
    }
    if (!inputDef.getFileName().toString().endsWith(".json")) {
      throw new ArgValidationException("invalid .json file '%s'".formatted(inputDef));
    }
    if (input == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-i", "--input"));
    }
    if (output != null && !output.getFileName().toString().endsWith(".vdb")) {
      throw new ArgValidationException("invalid .vdb file '%s'".formatted(output));
    }

    return new DbBuildArgs(input, inputDef, output, force);
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  vip-annotate database-build --definition <FILE> --input <FILE> [OPTIONS]
  vip-annotate database-build --help

Options:
  -d, --definition    FILE  JSON definition file for building the annotation database  (required)
                            See https://github.com/molgenis/vip-annotate for format.
  -i, --input         FILE  Input file path                                            (required)
  -o, --output        FILE  Output .vdb file path
  -f, --force         Overwrite existing output file if it exists""");
  }
}
