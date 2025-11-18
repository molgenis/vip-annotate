package org.molgenis.vipannotate.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.jspecify.annotations.Nullable;

public class ExceptionUtils {
  private ExceptionUtils() {}

  /**
   * Wraps {@link IOException} in {@link UncheckedIOException} and other checked exceptions in
   * {@link RuntimeException}. Subsequent exception are added as suppressed exceptions to the first
   * exception.
   *
   * <p>Throws the resulting exception if not <code>null</code>
   */
  public static void handleThrowable(@Nullable Throwable throwable) {
    if (throwable == null) {
      return;
    }
    switch (throwable) {
      case RuntimeException runtimeException -> throw runtimeException; // throw as is
      case Error error -> throw error; // throw as is
      case IOException ioException -> throw new UncheckedIOException(ioException);
      default -> throw new RuntimeException(throwable);
    }
  }
}
