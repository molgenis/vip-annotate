package org.molgenis.vipannotate.util;

public class AlleleUtils {
  private AlleleUtils() {}

  public static boolean isActg(String alt) {
    for (int i = 0, len = alt.length(); i < len; i++) {
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
