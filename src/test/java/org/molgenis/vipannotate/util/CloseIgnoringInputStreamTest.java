package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class CloseIgnoringInputStreamTest {
  @Test
  void newWithNullInputStream() {
    //noinspection DataFlowIssue,resource
    assertThrows(NullPointerException.class, () -> new CloseIgnoringInputStream(null));
  }

  @Test
  void close() throws IOException {
    InputStream inputStream = mock(InputStream.class);
    CloseIgnoringInputStream closeIgnoringInputStream = new CloseIgnoringInputStream(inputStream);
    closeIgnoringInputStream.close();
    verify(inputStream, never()).close();
  }
}
