package org.molgenis.vipannotate.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;

public class AnnotateArgsParser extends ArgsParser<AnnotateArgs> {
  @Override
  public AnnotateArgs parse(String[] args) {
    super.validate(args);

    Input input = null;
    Path annotationsDir = null;
    Output output = null;
    Boolean force = null;
    VcfType outputVcfType = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-a", "--annotations" -> {
          annotationsDir = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(annotationsDir)) {
            throw new ArgValidationException(
                "'%s' value '%s' does not exist".formatted(arg, annotationsDir));
          }
          if (!Files.isDirectory(annotationsDir)) {
            throw new ArgValidationException(
                "'%s' value '%s' is not a directory".formatted(arg, annotationsDir));
          }
        }
        case "-i", "--input" -> input = parseArgInputValue(args, i++, arg);
        case "-o", "--output" -> output = parseArgOutputValue(args, i++, arg);
        case "-O", "--output-type" -> {
          String outputType = parseArgValue(args, i++, arg);
          if (outputType.equals("v")) {
            outputVcfType = VcfType.UNCOMPRESSED;
          } else {
            if (outputType.equals("z")) {
              outputVcfType = VcfType.COMPRESSED;
            } else if (outputType.startsWith("z") && outputType.length() == 2) {
              int compressionLevel = Integer.parseInt(outputType.substring(1));
              outputVcfType = VcfType.fromCompressionLevel(compressionLevel);
            } else {
              throw new ArgValidationException(
                  "'%s' value '%s' is not a valid output type".formatted(arg, outputType));
            }
          }
        }
        case "-f", "--force" -> force = Boolean.TRUE;
        default -> throw new ArgValidationException("unknown option '%s'".formatted(arg));
      }
    }

    if (input == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-i", "--input"));
    }
    if (annotationsDir == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-a", "--annotations"));
    }
    if (output == null) {
      throw new ArgValidationException(
          "missing required option '%s' or '%s'".formatted("-o", "--output"));
    }

    if (output.path() != null && force == null && Files.exists(output.path())) {
      throw new ArgValidationException(
          "'%s' or '%s' value '%s' already exists".formatted("-o", "--output", output.path()));
    }
    return new AnnotateArgs(input, annotationsDir, output, force, outputVcfType);
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  apptainer run vip-annotate.sif annotate --annotations DIR --input FILE --output FILE [OPTIONS]
  apptainer run vip-annotate.sif annotate --help

Options:
  -a, --annotations DIR       Directory containing annotation database  (required)
  -i, --input       FILE      Input VCF file path; use '-' for stdin    (required)
  -o, --output      FILE      Output VCF file path; use '-' for stdout  (required)

  -O, --output-type v|z[0-9]  Output format                             (default: z)
                                Options:
                                  v      Uncompressed VCF
                                  z      Compressed VCF (default compression)
                                  z0-z9  Compressed VCF with compression levels 0-9

  -f, --force                 Overwrite existing output file if it exists""");
  }
}
