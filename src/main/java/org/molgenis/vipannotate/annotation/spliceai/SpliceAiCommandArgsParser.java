package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.ArgValidationException;
import org.molgenis.vipannotate.ArgsParser;
import org.molgenis.vipannotate.util.GraalVm;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

// TODO deduplicate, see DbCommandArgsPars
public class SpliceAiCommandArgsParser extends ArgsParser<SpliceAiCommandArgs> {
  private static final String COMMAND = "spliceai";

  @Override
  public SpliceAiCommandArgs parse(String[] args) {
    super.validate(args);

    Input input = null;
    Path ncbiGeneFile = null, faiFile = null;
    Path output = null;
    String regionsStr = null;
    Boolean force = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-i", "--input" -> input = parseArgInputValue(args, i++, arg);
        case "-n", "--ncbi-gene-index" -> {
          ncbiGeneFile = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(ncbiGeneFile)) {
            throw new ArgValidationException(
                "'%s' value '%s' does not exist".formatted(arg, ncbiGeneFile));
          }
        }
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
    if (ncbiGeneFile == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-n", "--ncbi_gene_index"));
    }
    if (output == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-o", "--output"));
    }
    if (faiFile == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-x", "--reference_index"));
    }
    if (force == null && Files.exists(output)) {
      throw new ArgValidationException(
          "'%s' or '%s' value '%s' already exists".formatted("-o", "--output", output));
    }

    return new SpliceAiCommandArgs(input, ncbiGeneFile, faiFile, output, regionsStr, force);
  }

  @Override
  protected void printUsage() {
    boolean isGraalRuntime = GraalVm.isGraalVmRuntime();
    String usage =
        isGraalRuntime
            ? "vip-annotate.exe %s".formatted(COMMAND)
            : "java -jar vip-annotate.jar %s".formatted(COMMAND);
    Logger.info(
        """
              vip-annotate v%s

              usage: %s [arguments]
                -i, --input           FILE                input file, e.g. spliceai_scores.masked.*.hg38.vcf.gz¹  (required                 )
                -n, --ncbi_gene_index FILE                tab-separated NCBI gene index file²                     (required                 )
                -x, --reference_index FILE                reference sequence index .fai file                      (required                 )
                -o, --output          FILE                output annotation database .zip file                    (optional, default: stdout)
                -r, --regions         chr|chr:beg-end[,…] comma-separated list of regions (inclusive, 1-based)    (optional                 )
                -f, --force                               overwrite existing output file                          (optional                 )

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
