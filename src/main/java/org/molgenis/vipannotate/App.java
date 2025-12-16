package org.molgenis.vipannotate;

import org.jspecify.annotations.Nullable;
import org.molgenis.streamvbyte.StreamVByteProvider;
import org.molgenis.vipannotate.cli.*;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.zstd.ZstdProvider;

public class App {
  static void main(String[] args) {
    try {
      AppArgs appArgs = new AppArgsParser().parse(args);
      configureLogger(appArgs.debugMode());
      getCommand(appArgs).run(appArgs.args());
    } catch (RuntimeException e) {
      handleException(e);
    } finally {
      StreamVByteProvider.INSTANCE.close();
      ZstdProvider.INSTANCE.close();
    }
  }

  private static void configureLogger(@Nullable Boolean debugMode) {
    if (debugMode != null && debugMode) {
      Logger.ENABLE_DEBUG_LOGGING = true;
    }
  }

  private static Command getCommand(AppArgs appArgs) {
    return switch (appArgs.command()) {
      case ANNOTATE -> new AnnotateCommand();
      case DATABASE_BUILD -> new DbBuildCommand();
      case DATABASE_DOWNLOAD -> new DbDownloadCommand();
    };
  }

  protected static void handleException(Exception e) {
    int exitStatus;
    if (e instanceof AppException appException) {
      Logger.error("%s", appException.getMessage());
      if (Logger.ENABLE_DEBUG_LOGGING) {
        appException.printStackTrace(System.err);
      }
      exitStatus = appException.getErrorCode().getCode();
    } else {
      String message = e.getMessage();
      if (message != null) {
        Logger.error("%s", message);
      }
      e.printStackTrace(System.err);
      Logger.error("an unexpected error occurred");
      exitStatus = 1;
    }
    System.exit(exitStatus);
  }
}
