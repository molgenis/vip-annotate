package org.molgenis.vipannotate;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.GraalVm;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor
public class AppDbCommandArgsParser extends ArgsParser<AppDbCommandArgs> {
  private final String command;

  @Override
  public AppDbCommandArgs parse(String[] args) {
    super.validate(args);

    Path inputFile = null, faiFile = null, outputFile = null;
    String regionsStr = null;
    Boolean force = null, debug = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-i":
        case "--input":
          inputFile = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(inputFile)) {
            Logger.error("'%s' value '%s' does not exist", arg, inputFile);
            System.exit(1);
          }
          break;
        case "-x":
        case "--reference-index":
          faiFile = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(faiFile)) {
            Logger.error("'%s' value '%s' does not exist", arg, faiFile);
            System.exit(1);
          }
          break;
        case "-o":
        case "--output":
          outputFile = Path.of(parseArgValue(args, i++, arg));
          break;
        case "-r":
        case "--regions":
          regionsStr = parseArgValue(args, i++, arg);
          break;
        case "-f":
        case "--force":
          force = Boolean.TRUE;
          break;
        case "-d":
        case "--debug":
          debug = Boolean.TRUE;
          break;
        default:
          Logger.error("unknown option '%s'", arg);
          System.exit(1);
          break;
      }
    }

    if (inputFile == null) {
      Logger.error("missing required option '%s' or '%s'", "-i", "--input");
      System.exit(1);
    }
    if (faiFile == null) {
      Logger.error("missing required option '%s' or '%s'", "-x", "--reference_index");
      System.exit(1);
    }
    if (outputFile == null) {
      Logger.error("missing required option '%s' or '%s'", "-o", "--output");
      System.exit(1);
    }

    if (force == null && Files.exists(outputFile)) {
      Logger.error("'%s' or '%s' value '%s' already exists", "-o", "--output", outputFile);
      System.exit(1);
    }

    return new AppDbCommandArgs(inputFile, faiFile, outputFile, regionsStr, force, debug);
  }

  @Override
  protected void printUsage() {
    boolean isGraalRuntime = GraalVm.isGraalVmRuntime();
    String usage =
        isGraalRuntime
            ? "vip-annotate.exe %s".formatted(command)
            : "java -jar vip-annotate.jar %s".formatted(command);
    Logger.info(
        """
              vip-annotate v%s

              usage: %s [arguments]
                -i, --input           FILE                input file, e.g. GRCh38_ncER_perc.bed.gz             (required)
                -x, --reference_index FILE                reference sequence index .fai file                   (required)
                -o, --output          FILE                output annotation database .zip file                 (required)
                -r, --regions         chr|chr:beg-end[,…] comma-separated list of regions (inclusive, 1-based) (optional)
                -f, --force                               overwrite existing output file                       (optional)

              usage: %s [arguments]
                -h, --help                     print this message

              usage: %s [arguments]
                -v, --version                  print version
              """,
        App.getVersion(), usage, usage, usage);
  }
}
