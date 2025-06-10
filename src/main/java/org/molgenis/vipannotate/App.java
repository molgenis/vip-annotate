package org.molgenis.vipannotate;

public class App {
  private static final String NAME = "vip-annotate";
  private static final String VERSION;

  static {
    String implementationVersion = AppAnnotate.class.getPackage().getImplementationVersion();
    VERSION = implementationVersion != null ? implementationVersion : "0.0.0-dev";
  }

  public static String getName() {
    return NAME;
  }

  public static String getVersion() {
    return VERSION;
  }
}
