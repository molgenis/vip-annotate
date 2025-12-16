package org.molgenis.vipannotate.cli;

import java.util.Arrays;
import org.molgenis.vipannotate.util.Logger;

public class AppArgsParser extends ArgsParser<AppArgs> {
  @Override
  public AppArgs parse(String[] args) {
    super.validate(args);

    int i = 0;
    Boolean debugMode = null;
    if (args[i].equals("-d") || args[i].equals("--debug")) {
      debugMode = true;
      i++;
    }

    AppArgs.Command command = parseArgCommandValue(args[i++]);
    String[] commandArgs = Arrays.copyOfRange(args, i, args.length);
    return new AppArgs(debugMode, command, commandArgs);
  }

  private AppArgs.Command parseArgCommandValue(String arg) {
    return switch (arg) {
      case "annotate" -> AppArgs.Command.ANNOTATE;
      case "database-build" -> AppArgs.Command.DATABASE_BUILD;
      case "database-download" -> AppArgs.Command.DATABASE_DOWNLOAD;
      default ->
          throw new ArgValidationException(
              "command '%s' unknown, valid values are [annotate, database-build, database-download]"
                  .formatted(arg));
    };
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  apptainer run vip-annotate.sif [OPTIONS] <command> [ARGS...]
  apptainer run vip-annotate.sif --version
  apptainer run vip-annotate.sif --help

Options:
  -d, --debug       Enable debug logging

Commands:
  annotate          Annotate vcf using an annotation database
  database-build    Build annotation database
  database-download Download annotation download""");
  }
}
