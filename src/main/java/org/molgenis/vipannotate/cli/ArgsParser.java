package org.molgenis.vipannotate.cli;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;

public abstract class ArgsParser<T> {
  public abstract T parse(String[] args);

  protected String parseArgValue(String[] args, int i, String arg) {
    if (i + 1 == args.length || (args[i + 1].length() > 1 && args[i + 1].startsWith("-"))) {
      throw new ArgValidationException("missing value for option '%s'".formatted(arg));
    }
    return args[i + 1];
  }

  protected Input parseArgInputValue(String[] args, int i, String arg) {
    String inputValue = parseArgValue(args, i, arg);
    try {
      return new Input(inputValue.equals("-") ? null : Path.of(inputValue));
    } catch (InvalidPathException e) {
      throw new ArgValidationException(
          "'%s' value '%s' is not a valid path".formatted(arg, inputValue));
    } catch (IllegalArgumentException e) {
      throw new ArgValidationException("'%s' value %s".formatted(arg, e.getMessage()));
    }
  }

  protected Output parseArgOutputValue(String[] args, int i, String arg) {
    String outputValue = parseArgValue(args, i, arg);
    try {
      return new Output(outputValue.equals("-") ? null : Path.of(outputValue));
    } catch (InvalidPathException e) {
      throw new ArgValidationException(
          "'%s' value '%s' is not a valid path".formatted(arg, outputValue));
    } catch (IllegalArgumentException e) {
      throw new ArgValidationException("'%s' value %s".formatted(arg, e.getMessage()));
    }
  }

  protected abstract void printUsage();

  protected void printVersion() {
    Logger.info("%s", AppMetadata.getVersion());
  }

  protected void validate(String[] args) {
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
