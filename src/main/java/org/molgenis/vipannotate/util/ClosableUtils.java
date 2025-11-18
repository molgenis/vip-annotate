package org.molgenis.vipannotate.util;

import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class ClosableUtils {
  private ClosableUtils() {}

  /** Closes a {@link AutoCloseable}. */
  public static void close(@Nullable AutoCloseable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Throwable throwable) {
        ExceptionUtils.handleThrowable(throwable);
      }
    }
  }

  /** Closes multiple {@link AutoCloseable}. */
  public static void closeAll(@Nullable AutoCloseable... closeables) {
    closeAll(Arrays.asList(closeables));
  }

  /** Closes multiple {@link AutoCloseable}. */
  public static void closeAll(List<? extends @Nullable AutoCloseable> closeables) {
    Throwable firstThrowable = null;

    for (AutoCloseable closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (Throwable throwable) {
          if (firstThrowable == null) {
            firstThrowable = throwable;
          } else {
            firstThrowable.addSuppressed(throwable);
          }
        }
      }
    }

    ExceptionUtils.handleThrowable(firstThrowable);
  }
}
