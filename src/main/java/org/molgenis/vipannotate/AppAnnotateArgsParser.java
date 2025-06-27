package org.molgenis.vipannotate;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.GraalVm;
import org.molgenis.vipannotate.util.Logger;

public class AppAnnotateArgsParser extends ArgsParser<AppAnnotateArgs> {
  @Override
  public AppAnnotateArgs parse(String[] args) {
    super.validate(args);

    Path input = null, annotationsDir = null, output = null;
    Boolean force = null;
    Boolean debug = null;
    VcfType outputVcfType = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-i":
        case "--input":
          input = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(input)) {
            Logger.error("'%s' value '%s' does not exist", arg, input);
            System.exit(1);
          }
          break;
        case "-a":
        case "--annotations-dir":
          annotationsDir = Path.of(parseArgValue(args, i++, arg));
          if (Files.notExists(annotationsDir)) {
            Logger.error("'%s' value '%s' does not exist", arg, annotationsDir);
            System.exit(1);
          }
          if (!Files.isDirectory(annotationsDir)) {
            Logger.error("'%s' value '%s' is not a directory", arg, annotationsDir);
            System.exit(1);
          }
          break;
        case "-o":
        case "--output":
          output = Path.of(parseArgValue(args, i++, arg));
          break;
        case "-O":
        case "--output-type":
          String outputType = parseArgValue(args, i++, arg);
          if (outputType.equals("v")) {
            outputVcfType = VcfType.UNCOMPRESSED;
          } else {
            if (outputType.equals("z")) {
              outputVcfType = VcfType.COMPRESSED;
            } else if (outputType.startsWith("z") & outputType.length() == 2) {
              int compressionLevel = Integer.parseInt(outputType.substring(1));
              outputVcfType = VcfType.fromCompressionLevel(compressionLevel);
            } else {
              Logger.error("'%s' value '%s' is not a valid output type", arg, outputType);
              System.exit(1);
            }
          }
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

    if (annotationsDir == null) {
      Logger.error("missing required option '%s' or '%s'", "-a", "--annotations_dir");
      System.exit(1);
    }
    if (output != null && force == null && Files.exists(output)) {
      Logger.error("'%s' or '%s' value '%s' already exists", "-o", "--output", output);
      System.exit(1);
    }
    return new AppAnnotateArgs(input, annotationsDir, output, force, debug, outputVcfType);
  }

  private String parseArgValue(String[] args, int i, String arg) {
    if (i + 1 == args.length || args[i + 1].startsWith("-")) {
      Logger.error("missing value for option '%s'", arg);
      System.exit(1);
    }
    return args[i + 1];
  }

  @Override
  protected void printUsage() {
    boolean isGraalRuntime = GraalVm.isGraalRuntime();
    String usage = isGraalRuntime ? "vip-annotate.exe" : "java -jar vip-annotate.jar";
    Logger.info(
        """
        vip-annotate v%s

        usage: java -jar vip-annotate.jar [arguments]
          -i, --input           FILE     input VCF file                           (optional, default: stdin       )
          -a, --annotations-dir DIR      annotation database directory            (required                       )
          -o, --output          FILE     output VCF file                          (optional, default: stdout      )
          -O, --output-type     v|z[0-9] uncompressed VCF (v), compressed VCF (z) (optional, default: uncompressed)
                                         with optional compression level 0-9

        usage: %s [arguments]
          -h, --help                     print this message

        usage: %s [arguments]
          -v, --version                  print version
        """,
        App.getVersion(), usage, usage);
  }

  @Override
  protected void printVersion() {
    Logger.info("%s\n", App.getVersion());
  }
}
