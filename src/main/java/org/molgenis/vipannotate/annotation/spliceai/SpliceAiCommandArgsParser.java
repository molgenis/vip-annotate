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
  private static final String COMMAND = "spliceai";

  @Override
  public SpliceAiCommandArgs parse(String[] args) {
    // FIXME proper parsing
    Path inputFile = Path.of(args[1]);
    Path ncbiGeneFile = Path.of(args[3]);
    Path faiFile = Path.of(args[5]);
    Path outputFile = Path.of(args[7]);

    if (args.length == 9 && args[8].equals("--force")) {
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

    return new SpliceAiCommandArgs(inputFile, ncbiGeneFile, faiFile, outputFile);
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
                -i, --input           FILE                input file, e.g. spliceai_scores.masked.*.hg38.vcf.gz (required)
                -g, --gene_index      FILE                tab-separated NCBI gene index file¹                   (required)
                -x, --reference_index FILE                reference sequence index .fai file                    (required)
                -o, --output          FILE                output annotation database .zip file                  (required)
                -r, --regions         chr|chr:beg-end[,…] comma-separated list of regions (inclusive, 1-based)  (optional)
                -f, --force                               overwrite existing output file                        (optional)

              usage: %s [arguments]
                -h, --help                     print this message

              usage: %s [arguments]
                -v, --version                  print version

              ¹ available from https://www.ncbi.nlm.nih.gov/datasets/gene/taxon/9606 with columns 'Gene ID' and 'Symbol'
              """,
        App.getVersion(), usage, usage, usage);
  }
}
