package org.molgenis.vipannotate.util;

public class GraalVm {
  private static final String GRAAL_IMAGE_CODE_KEY = "org.graalvm.nativeimage.imagecode";
  private static final String GRAAL_IMAGE_RUNTIME = "runtime";

  /** Returns true if the current process is executing at image runtime. */
  public static boolean isGraalRuntime() {
    return GRAAL_IMAGE_RUNTIME.equals(System.getProperty(GRAAL_IMAGE_CODE_KEY));
  }
}
