package org.molgenis.vipannotate;

import org.molgenis.vipannotate.util.Logger;

public class App {
  private static final String NAME = "vip-annotate";
  private static final String VERSION;

  static {
    Package appAnnotatePackage = AppAnnotate.class.getPackage();
    String implementationVersion =
        appAnnotatePackage != null ? appAnnotatePackage.getImplementationVersion() : null;
    VERSION = implementationVersion != null ? implementationVersion : "0.0.0-dev";
  }

  public static String getName() {
    return NAME;
  }

  public static String getVersion() {
    return VERSION;
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
