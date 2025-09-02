package org.molgenis.vipannotate;

import org.molgenis.vipannotate.util.Logger;

public abstract class ArgsParser<T> {
  public abstract T parse(String[] args);

  protected String parseArgValue(String[] args, int i, String arg) {
    if (i + 1 == args.length || args[i + 1].startsWith("-")) {
      Logger.error("missing value for option '%s'", arg);
      System.exit(1);
    }
    return args[i + 1];
  }

  protected abstract void printUsage();

  protected void printVersion() {
    Logger.info("%s\n", App.getVersion());
  }

  protected void validate(String[] args) {
    if (args.length == 0) {
      Logger.error("missing command line argument");
      printUsage();
      System.exit(1);
    }
    if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
      printUsage();
      System.exit(0);
    }
    if (args.length == 1 && (args[0].equals("-v") || args[0].equals("--version"))) {
      printVersion();
      System.exit(0);
    }
  }
}
