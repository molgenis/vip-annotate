package org.molgenis.vipannotate.util;

public class RuntimeDetector {
  public static boolean isNativeImage() {
    return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
  }
}
