package org.molgenis.vipannotate.util;

import java.io.PrintStream;

public class Logger {
  public static boolean REDIRECT_STDOUT_TO_STDERR = false;
  public static boolean ENABLE_DEBUG_LOGGING = false;

  public static void info(String format, Object... args) {
    PrintStream printStream = REDIRECT_STDOUT_TO_STDERR ? System.err : System.out;
    log(printStream, format, args);
  }

  public static void error(String format, Object... args) {
    System.err.print("error: ");
    log(System.err, format, args);
  }

  private static void log(PrintStream printStream, String format, Object... args) {
    printStream.printf(format, args);
    printStream.print('\n');
  }
}
