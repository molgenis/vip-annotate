package org.molgenis.vipannotate.util;

import java.nio.file.OpenOption;

public final class OpenOptions {
  /**
   * Encapsulates access to the internal JDK API {@code com.sun.nio.file.ExtendedOpenOption.DIRECT},
   * ensuring the corresponding compiler warning is generated from only this source file.
   */
  public static final OpenOption DIRECT = com.sun.nio.file.ExtendedOpenOption.DIRECT;

  private OpenOptions() {}
}
