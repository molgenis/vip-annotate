package org.molgenis.vipannotate.util;

public class GraalVm {
  private static final String GRAALVM_IMAGE_CODE_KEY = "org.graalvm.nativeimage.imagecode";
  private static final String GRAALVM_IMAGE_RUNTIME = "runtime";

  /** Returns true if the current process is executing at image runtime. */
  public static boolean isGraalVmRuntime() {
    return GRAALVM_IMAGE_RUNTIME.equals(System.getProperty(GRAALVM_IMAGE_CODE_KEY));
  }
}
