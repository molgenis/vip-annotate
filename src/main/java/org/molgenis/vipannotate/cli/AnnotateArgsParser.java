package org.molgenis.vipannotate.cli;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;

public class AnnotateArgsParser extends ArgsParser<AnnotateArgs> {
  @Override
  public AnnotateArgs parse(String[] args) {
    super.validate(args);

    Input input = null;
    Set<Path> annotationFiles = new LinkedHashSet<>();
    Output output = null;
    Boolean force = null;
    VcfType outputVcfType = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-a", "--annotations" -> {
          String value = parseArgValue(args, i++, arg);
          annotationFiles.addAll(
              parseArgAnnotationFiles(value, arg).stream().map(Path::normalize).toList());
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
    if (annotationFiles.isEmpty()) {
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
    return new AnnotateArgs(input, List.copyOf(annotationFiles), output, force, outputVcfType);
  }

  private static List<Path> parseArgAnnotationFiles(String value, String arg) {
    boolean hasGlob = value.indexOf('*') >= 0 || value.indexOf('?') >= 0 || value.indexOf('[') >= 0;

    if (!hasGlob) {
      Path path = Path.of(value);
      validateAnnotationFile(path, arg);
      return List.of(path);
    }

    // Split at the last path separator, before creating a Path from the glob.
    int separator = Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));

    String parentValue = separator < 0 ? "." : value.substring(0, separator);
    String filePattern = value.substring(separator + 1);

    Path parent = Path.of(parentValue.isEmpty() ? value.substring(0, 1) : parentValue);

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);

    try (Stream<Path> paths = Files.list(parent)) {
      List<Path> matches =
          paths
              .filter(p -> matcher.matches(p.getFileName()))
              .filter(Files::isRegularFile)
              .sorted()
              .toList();

      if (matches.isEmpty()) {
        throw new ArgValidationException(
            "'%s' value '%s' matched no annotation files".formatted(arg, value));
      }

      return matches;
    } catch (IOException e) {
      throw new ArgValidationException(
          "cannot read annotation directory '%s': %s".formatted(parent, e.getMessage()));
    }
  }

  private static void validateAnnotationFile(Path path, String arg) {
    if (!Files.exists(path)) {
      throw new ArgValidationException("'%s' value '%s' does not exist".formatted(arg, path));
    }

    if (!Files.isRegularFile(path)) {
      throw new ArgValidationException(
          "'%s' value '%s' is not a regular file".formatted(arg, path));
    }
  }

  @Override
  protected void printUsage() {
    Logger.info(
"""
Usage:
  vip-annotate annotate --annotations FILE --input FILE --output FILE [OPTIONS]
  vip-annotate annotate --help

Options:
  -a, --annotations FILE      Annotation database file or glob pattern; may be specified multiple times (required)
  -i, --input       FILE      Input VCF file path; use '-' for stdin                                    (required)
  -o, --output      FILE      Output VCF file path; use '-' for stdout                                  (required)

  -O, --output-type v|z[0-9]  Output format                                                             (default: z)
                                Options:
                                  v      Uncompressed VCF
                                  z      Compressed VCF (default compression)
                                  z0-z9  Compressed VCF with compression levels 0-9

  -f, --force                 Overwrite existing output file if it exists""");
  }
}
