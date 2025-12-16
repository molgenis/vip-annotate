package org.molgenis.vipannotate;

public class AppMetadata {
  private static final String NAME = "vip-annotate";
  private static final String VERSION;

  static {
    Package appAnnotatePackage = App.class.getPackage();
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
}
