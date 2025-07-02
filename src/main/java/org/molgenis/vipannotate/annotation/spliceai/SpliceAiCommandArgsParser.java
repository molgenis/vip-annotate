package org.molgenis.vipannotate.annotation.spliceai;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.ArgsParser;
import org.molgenis.vipannotate.util.GraalVm;
import org.molgenis.vipannotate.util.Logger;

public class SpliceAiCommandArgsParser extends ArgsParser<SpliceAiCommandArgs> {
  private static final String COMMAND = "remm";

  @Override
  public SpliceAiCommandArgs parse(String[] args) {
    // FIXME proper parsing
    Path inputFile = Path.of(args[1]);
    Path faiFile = Path.of(args[3]);
    Path outputFile = Path.of(args[5]);

    if (args.length == 7 && args[6].equals("--force")) {
      try {
        Files.deleteIfExists(outputFile);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      if (Files.exists(outputFile)) {
        throw new IllegalArgumentException("Output file %s already exists".formatted(outputFile));
      }
    }

    return new SpliceAiCommandArgs(inputFile, faiFile, outputFile);
  }

  @Override
  protected void printUsage() {
    boolean isGraalRuntime = GraalVm.isGraalRuntime();
    String usage =
        isGraalRuntime
            ? "vip-annotate.exe %s".formatted(COMMAND)
            : "java -jar vip-annotate.jar %s".formatted(COMMAND);
    Logger.info(
        """
              vip-annotate v%s

              usage: %s [arguments]
                -i, --input           FILE     input file, e.g. spliceai_scores.masked.*.hg38.vcf.gz (required)
                -r, --reference_index FILE     reference sequence index .fai file                    (required)
                -o, --output          FILE     output annotation database .zip file                  (required)
                -f, --force                    overwrite existing output file                        (optional)

              usage: %s [arguments]
                -h, --help                     print this message

              usage: %s [arguments]
                -v, --version                  print version
              """,
        App.getVersion(), usage, usage, usage);
  }
}
