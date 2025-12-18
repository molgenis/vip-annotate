package org.molgenis.vipannotate.util;

public class CharSequenceUtils {
  public static boolean equals(CharSequence thisSequence, CharSequence thatSequence) {
    int len = thisSequence.length();
    if (len != thatSequence.length()) {
      return false;
    }
    for (int i = 0; i < len; i++) {
      if (thisSequence.charAt(i) != thatSequence.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
