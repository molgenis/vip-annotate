package org.molgenis.vipannotate.util;

public class AlleleUtils {
  private AlleleUtils() {}

  public static boolean isActg(CharSequence alt) {
    int len = alt.length();
    if (len == 0) {
      return false;
    }

    for (int i = 0; i < len; i++) {
      switch (alt.charAt(i)) {
        case 'A', 'C', 'T', 'G' -> {}
        default -> {
          return false;
        }
      }
    }

    return true;
  }
}
