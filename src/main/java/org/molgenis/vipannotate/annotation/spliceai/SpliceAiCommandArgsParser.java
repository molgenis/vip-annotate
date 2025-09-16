package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.ArgsParser;
import org.molgenis.vipannotate.util.GraalVm;
import org.molgenis.vipannotate.util.Logger;

// TODO deduplicate, see DbCommandArgsPars
public class SpliceAiCommandArgsParser extends ArgsParser<SpliceAiCommandArgs> {
  private static final String COMMAND = "spliceai";

  @Override
  public SpliceAiCommandArgs parse(String[] args) {
    super.validate(args);

    Path inputFile = null, ncbiGeneFile = null, faiFile = null, outputFile = null;
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
        case "-n":
        case "--ncbi-gene-index":
          ncbiGeneFile = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(ncbiGeneFile)) {
            Logger.error("'%s' value '%s' does not exist", arg, ncbiGeneFile);
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
    if (ncbiGeneFile == null) {
      Logger.error("missing required option '%s' or '%s'", "-n", "--ncbi_gene_index");
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

    return new SpliceAiCommandArgs(
        inputFile, ncbiGeneFile, faiFile, outputFile, regionsStr, force, debug);
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
                -i, --input           FILE                input file, e.g. spliceai_scores.masked.*.hg38.vcf.gz¹ (required)
                -n, --ncbi_gene_index FILE                tab-separated NCBI gene index file²                    (required)
                -x, --reference_index FILE                reference sequence index .fai file                     (required)
                -o, --output          FILE                output annotation database .zip file                   (required)
                -r, --regions         chr|chr:beg-end[,…] comma-separated list of regions (inclusive, 1-based)   (optional)
                -f, --force                               overwrite existing output file                         (optional)

              usage: %s [arguments]
                -h, --help                     print this message

              usage: %s [arguments]
                -v, --version                  print version

              ¹ --input is required twice, once for snv.vcf.gz and once for indel.vcf.gz
              ² available from https://www.ncbi.nlm.nih.gov/datasets/gene/taxon/9606 with columns 'Gene ID' and 'Symbol'
              """,
        App.getVersion(), usage, usage, usage);
  }
}
