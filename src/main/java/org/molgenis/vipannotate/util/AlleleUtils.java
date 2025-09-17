package org.molgenis.vipannotate.util;

public class AlleleUtils {
  private AlleleUtils() {}

  public static boolean isActg(String alt) {
    int len = alt.length();
    if (len == 0) {
      return false;
    }

    for (int i = 0; i < len; i++) {
      switch (alt.charAt(i)) {
        case 'A':
        case 'C':
        case 'T':
        case 'G':
          break;
        default:
          return false;
      }
    }

    return true;
  }
}
