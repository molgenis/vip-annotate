package org.molgenis.vcf.annotate;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vcf.annotate.util.Logger;

public class AppArgsParser {

  private AppArgsParser() {}

  public static AppArgs parse(String[] args) {
    if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
      printUsage();
      System.exit(0);
    }
    if (args.length == 1 && (args[0].equals("-v") || args[0].equals("--version"))) {
      printVersion();
      System.exit(0);
    }

    Path input = null, annotations = null, output = null;
    Boolean force = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-i":
        case "--input":
          input = parseArgValue(args, i++, arg);
          if (Files.notExists(input)) {
            Logger.error("'%s' value '%s' does not exist", arg, input);
            System.exit(1);
          }
          break;
        case "-a":
        case "--annotations":
          annotations = parseArgValue(args, i++, arg);
          if (Files.notExists(annotations)) {
            Logger.error("'%s' value '%s' does not exist", arg, annotations);
            System.exit(1);
          }
          break;
        case "-o":
        case "--output":
          output = parseArgValue(args, i++, arg);
          break;
        case "-f":
        case "--force":
          force = Boolean.TRUE;
          break;
        default:
          Logger.error("unknown option '%s'", arg);
          System.exit(1);
          break;
      }
    }

    if (annotations == null) {
      Logger.error("missing required option '%s' or '%s'", "-a", "--annotations");
      System.exit(1);
    }
    if (output != null && force == null && Files.exists(output)) {
      Logger.error("'%s' or '%s' value '%s' already exists", "-o", "--output", output);
      System.exit(1);
    }
    return new AppArgs(input, annotations, output, force);
  }

  private static Path parseArgValue(String[] args, int i, String arg) {
    if (i + 1 == args.length || args[i + 1].startsWith("-")) {
      Logger.error("missing value for option '%s'", arg);
      System.exit(1);
    }
    return Path.of(args[i + 1]);
  }

  private static void printUsage() {
    Logger.info(
        """
        vip-annotate v%s

        usage: java -jar vip-annotate.jar [arguments]
          -i, --input       <file>   input VCF file           (optional, default: stdin)
          -a, --annotations <file>   annotation database file
          -o, --output      <file>   output VCF file          (optional, default: stdout)

        usage: java -jar vip-annotate.jar [arguments]
          -h, --help                 print this message

        usage: java -jar vip-annotate.jar [arguments]
          -v, --version              print version
        """,
        getVersion());
  }

  private static void printVersion() {
    Logger.info("%s\n", getVersion());
  }

  private static String getVersion() {
    // TODO update pom.xml as described in https://stackoverflow.com/a/2713013
    String implementationVersion = App.class.getPackage().getImplementationVersion();
    return implementationVersion != null ? implementationVersion : "0.0.0-dev";
  }
}
