package org.molgenis.vipannotate.util;

import java.io.PrintStream;
import org.apache.fory.logging.LoggerFactory;

public class Logger {
  static {
    LoggerFactory.disableLogging(); // disable apache fory logging
  }

  public static boolean REDIRECT_STDOUT_TO_STDERR = false;
  public static boolean ENABLE_DEBUG_LOGGING = false;

  public static boolean isDebugEnabled() {
    return ENABLE_DEBUG_LOGGING;
  }

  public static void debug(String format, Object arg) {
    if (ENABLE_DEBUG_LOGGING) {
      PrintStream printStream = getPrintStream();
      printStream.print("debug: ");
      log(printStream, format, arg);
    }
  }

  public static void debug(String format, Object... args) {
    if (ENABLE_DEBUG_LOGGING) {
      PrintStream printStream = getPrintStream();
      printStream.print("debug: ");
      log(printStream, format, args);
    }
  }

  public static void info(String format, Object... args) {
    PrintStream printStream = getPrintStream();
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

  private static PrintStream getPrintStream() {
    return REDIRECT_STDOUT_TO_STDERR ? System.err : System.out;
  }
}
