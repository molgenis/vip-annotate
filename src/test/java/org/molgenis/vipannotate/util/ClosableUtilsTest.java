package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class ClosableUtilsTest {
  @Test
  void close() throws Exception {
    RuntimeException runtimeException = mock(RuntimeException.class);
    AutoCloseable autoCloseable = mock(AutoCloseable.class);
    doThrow(runtimeException).when(autoCloseable).close();
    try {
      ClosableUtils.close(autoCloseable);
    } catch (RuntimeException actualRuntimeException) {
      assertEquals(runtimeException, actualRuntimeException);
    }
  }

  @Test
  void closeNoExceptions() {
    assertDoesNotThrow(() -> ClosableUtils.close(mock(AutoCloseable.class)));
  }

  @Test
  void closeAll() throws Exception {
    AutoCloseable autoCloseable = mock(AutoCloseable.class);

    RuntimeException runtimeException = mock(RuntimeException.class);
    AutoCloseable autoCloseableThrows = mock(AutoCloseable.class);
    doThrow(runtimeException).when(autoCloseableThrows).close();

    IOException ioException = mock(IOException.class);
    AutoCloseable autoCloseableThrowsIoException = mock(AutoCloseable.class);
    doThrow(ioException).when(autoCloseableThrowsIoException).close();

    try {
      ClosableUtils.closeAll(
          autoCloseable, autoCloseableThrows, null, autoCloseableThrowsIoException);
    } catch (Throwable t) {
      assertAll(
          () -> assertEquals(runtimeException, t),
          () -> verify(runtimeException).addSuppressed(ioException));
    }
  }

  @Test
  void closeAllNoExceptions() {
    assertDoesNotThrow(
        () -> ClosableUtils.closeAll(mock(AutoCloseable.class), mock(AutoCloseable.class)));
  }

  @Test
  void closeAllError() throws Exception {
    Error error = mock(Error.class);
    AutoCloseable autoCloseable = mock(AutoCloseable.class);
    doThrow(error).when(autoCloseable).close();
    try {
      ClosableUtils.closeAll(autoCloseable);
    } catch (Error actualError) {
      assertEquals(error, actualError);
    }
  }

  @Test
  void closeAllioException() throws Exception {
    IOException ioException = mock(IOException.class);
    AutoCloseable autoCloseable = mock(AutoCloseable.class);
    doThrow(ioException).when(autoCloseable).close();
    try {
      ClosableUtils.closeAll(autoCloseable);
    } catch (UncheckedIOException uncheckedIOException) {
      assertEquals(ioException, uncheckedIOException.getCause());
    }
  }

  @Test
  void closeAllOtherUncheckedException() throws Exception {
    Exception exception = mock(Exception.class);
    AutoCloseable autoCloseable = mock(AutoCloseable.class);
    doThrow(exception).when(autoCloseable).close();
    try {
      ClosableUtils.closeAll(autoCloseable);
    } catch (RuntimeException runtimeException) {
      assertEquals(exception, runtimeException.getCause());
    }
  }
}
