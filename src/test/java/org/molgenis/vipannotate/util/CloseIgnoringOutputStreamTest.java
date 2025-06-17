package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

class CloseIgnoringOutputStreamTest {
  @Test
  void newWithNullInputStream() {
    //noinspection DataFlowIssue,resource
    assertThrows(NullPointerException.class, () -> new CloseIgnoringOutputStream(null));
  }

  @Test
  void close() throws IOException {
    OutputStream outputStream = mock(OutputStream.class);
    CloseIgnoringOutputStream closeIgnoringOutputStream =
        new CloseIgnoringOutputStream(outputStream);
    closeIgnoringOutputStream.close();
    verify(outputStream, never()).close();
  }
}
