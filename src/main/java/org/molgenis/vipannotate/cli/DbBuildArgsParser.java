package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

public class DbBuildArgsParser extends ArgsParser<DbBuildArgs> {
  @Override
  public DbBuildArgs parse(String[] args) {
    super.validate(args);

    Input input = null;
    Path inputRecipe = null;
    Path outputDir = null;
    Boolean force = null;

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-i", "--input" -> input = parseArgInputValue(args, i++, arg);
        case "-r", "--recipe" -> inputRecipe = Path.of(parseArgValue(args, i++, arg));
        case "-o", "--output-dir" -> outputDir = Path.of(parseArgValue(args, i++, arg));
        case "-f", "--force" -> force = Boolean.TRUE;
        default -> throw new ArgValidationException("unknown option '%s'".formatted(arg));
      }
    }

    if (input == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-i", "--input"));
    }
    if (inputRecipe == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-r", "--recipe"));
    }
    if (!inputRecipe.getFileName().toString().endsWith(".json")) {
      throw new ArgValidationException("invalid .json recipe file '%s'".formatted(inputRecipe));
    }

    return new DbBuildArgs(input, inputRecipe, outputDir, force);
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  vip-annotate database-build --input <FILE> --recipe <FILE> [OPTIONS]
  vip-annotate database-build --help

Options:
  -i, --input         FILE  Input file path; use '-' for stdin  (required)
  -r, --recipe        FILE  Database build recipe (.json)       (required)
  -o, --output-dir    DIR   Output directory
  -f, --force         Overwrite existing output file if it exists""");
  }
}
