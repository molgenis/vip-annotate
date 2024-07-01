package org.molgenis.vipannotate;

import java.util.Arrays;
import org.molgenis.vipannotate.util.Logger;

public class AppDbArgsParser extends ArgsParser<AppDbArgs> {
  @Override
  public AppDbArgs parse(String[] args) {
    super.validate(args);

    int i = 0;
    Boolean debugMode = null;
    if (args[i].equals("-d") || args[i].equals("--debug")) {
      debugMode = true;
      i++;
    }

    AppDbArgs.Command command = parseArgCommandValue(args[i++]);
    String[] commandArgs = Arrays.copyOfRange(args, i, args.length);
    return new AppDbArgs(debugMode, command, commandArgs);
  }

  private AppDbArgs.Command parseArgCommandValue(String arg) {
    return switch (arg) {
      case "fathmm_mkl" -> AppDbArgs.Command.FATHMM_MKL;
      case "gnomad" -> AppDbArgs.Command.GNOMAD_SHORT_VARIANT;
      case "ncer" -> AppDbArgs.Command.NCER;
      case "phylop" -> AppDbArgs.Command.PHYLOP;
      case "remm" -> AppDbArgs.Command.REMM;
      case "spliceai" -> AppDbArgs.Command.SPLICEAI;
      default ->
          throw new ArgValidationException(
              "command '%s' unknown, valid values are [fathmm_mkl, gnomad, ncer, phylop, remm, spliceai]"
                  .formatted(arg));
    };
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  vip-annotate-db [OPTIONS] <command> [ARGS...]

Options:
  -d, --debug       Enable debug logging
  -v, --version     Show version and exit
  -h, --help        Show this help message and exit

Commands:
  fathmm_mkl        Build FATHMM-MKL database
  gnomad            Build gnomAD database
  ncer              Build NCER database
  phylop            Build PhyloP database
  remm              Build ReMM database
  spliceai          Build SpliceAI database""");
  }
}
