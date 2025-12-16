package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.molgenis.vipannotate.util.Logger;

public class DbDownloadArgsParser extends ArgsParser<DbDownloadArgs> {
  @Override
  public DbDownloadArgs parse(String[] args) {
    super.validate(args);

    Path outputDir = null;
    Boolean force = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-o", "--output" -> outputDir = Path.of(parseArgValue(args, i++, arg));
        case "-f", "--force" -> force = Boolean.TRUE;
        default -> throw new ArgValidationException("unknown option '%s'".formatted(arg));
      }
    }

    if (outputDir == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-o", "--output"));
    }
    return new DbDownloadArgs(outputDir, force);
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  apptainer run vip-annotate.sif database-download --output DIR [OPTIONS]
  apptainer run vip-annotate.sif database-download --help

Options:
  -o, --output      DIR      Output directory  (required)
  -f, --force                Overwrite existing output files if they exist""");
  }
}
