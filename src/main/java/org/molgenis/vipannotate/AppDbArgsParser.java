package org.molgenis.vipannotate;

import java.util.Arrays;
import org.molgenis.vipannotate.util.Logger;

public class AppDbArgsParser extends ArgsParser<AppDbArgs> {
  @Override
  public AppDbArgs parse(String[] args) {
    super.validate(args);

    AppDbArgs.Command command = parseArgCommandValue(args[0]);
    String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
    return new AppDbArgs(command, commandArgs);
  }

  private AppDbArgs.Command parseArgCommandValue(String arg) {
    return switch (arg) {
      case "gnomad" -> AppDbArgs.Command.GNOMAD;
      case "ncer" -> AppDbArgs.Command.NCER;
      case "phylop" -> AppDbArgs.Command.PHYLOP;
      case "remm" -> AppDbArgs.Command.REMM;
      default -> {
        Logger.error("command '%s' unknown, valid values are [gnomad, ncer, phylop, remm]");
        System.exit(1);
        throw new RuntimeException(); // unreachable, but suppresses IntelliJ warning
      }
    };
  }

  @Override
  protected void printUsage() {
    String usage = "java -jar vip-annotate-db.jar";
    Logger.info(
        """
        vip-annotate-db v%s

        usage: %s gnomad [arguments]
        usage: %s ncer   [arguments]
        usage: %s phylop [arguments]
        usage: %s remm   [arguments]
        usage: %s -h, --help                     print this message
        usage: %s -v, --version                  print version
        """,
        App.getVersion(), usage, usage);
  }

  @Override
  protected void printVersion() {
    Logger.info("%s\n", App.getVersion());
  }
}
