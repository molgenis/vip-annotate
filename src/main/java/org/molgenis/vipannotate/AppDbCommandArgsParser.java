package org.molgenis.vipannotate;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.GraalVm;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor
public class AppDbCommandArgsParser extends ArgsParser<AppDbCommandArgs> {
  private final String command;

  @Override
  public AppDbCommandArgs parse(String[] args) {
    super.validate(args);

    Input input = null;
    Path faiFile = null;
    Path output = null;
    String regionsStr = null;
    Boolean force = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-i", "--input" -> input = parseArgInputValue(args, i++, arg);
        case "-x", "--reference-index" -> {
          faiFile = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(faiFile)) {
            throw new ArgValidationException(
                "'%s' value '%s' does not exist".formatted(arg, faiFile));
          }
        }
        case "-o", "--output" -> output = Path.of(parseArgValue(args, i++, arg));
        case "-r", "--regions" -> regionsStr = parseArgValue(args, i++, arg);
        case "-f", "--force" -> force = Boolean.TRUE;
        default -> throw new ArgValidationException("unknown option '%s'".formatted(arg));
      }
    }

    if (input == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-i", "--input"));
    }
    if (faiFile == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-x", "--reference-index"));
    }
    if (output == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-o", "--output"));
    }

    if (force == null && Files.exists(output)) {
      throw new ArgValidationException(
          "'%s' or '%s' value '%s' already exists".formatted("-o", "--output", output));
    }

    return new AppDbCommandArgs(input, faiFile, output, regionsStr, force);
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
                -i, --input           FILE                input file, e.g. GRCh38_ncER_perc.bed.gz             (required                 )
                -x, --reference-index FILE                reference sequence index .fai file                   (required                 )
                -o, --output          FILE                output annotation database .zip file                 (optional, default: stdout)
                -r, --regions         chr|chr:beg-end[,…] comma-separated list of regions (inclusive, 1-based) (optional                 )
                -f, --force                               overwrite existing output file                       (optional                 )

              usage: %s [arguments]
                -h, --help                     print this message

              usage: %s [arguments]
                -v, --version                  print version
              """,
        App.getVersion(), usage, usage, usage);
  }
}
