package org.molgenis.vipannotate.cli;

import java.util.Arrays;
import org.molgenis.vipannotate.util.Logger;

public class DbBuildArgsParser extends ArgsParser<DbBuildArgs> {
  @Override
  public DbBuildArgs parse(String[] args) {
    super.validate(args);

    int i = 0;
    DbBuildArgs.Command command = parseArgCommandValue(args[i++]);
    String[] commandArgs = Arrays.copyOfRange(args, i, args.length);
    return new DbBuildArgs(command, commandArgs);
  }

  private DbBuildArgs.Command parseArgCommandValue(String arg) {
    return switch (arg) {
      case "fathmm_mkl" -> DbBuildArgs.Command.FATHMM_MKL;
      case "gnomad" -> DbBuildArgs.Command.GNOMAD_SHORT_VARIANT;
      case "ncer" -> DbBuildArgs.Command.NCER;
      case "phylop" -> DbBuildArgs.Command.PHYLOP;
      case "remm" -> DbBuildArgs.Command.REMM;
      case "spliceai" -> DbBuildArgs.Command.SPLICEAI;
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
  apptainer run vip-annotate.sif database-build <command> [ARGS...]
  apptainer run vip-annotate.sif database-build --help

Commands:
  fathmm_mkl        Build FATHMM-MKL database
  gnomad            Build gnomAD database
  ncer              Build NCER database
  phylop            Build PhyloP database
  remm              Build ReMM database
  spliceai          Build SpliceAI database""");
  }
}
