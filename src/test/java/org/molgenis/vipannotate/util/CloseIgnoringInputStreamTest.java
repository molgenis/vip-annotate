package org.molgenis.vipannotate.util;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class CloseIgnoringInputStreamTest {
  @Test
  void close() throws IOException {
    InputStream inputStream = mock(InputStream.class);
    CloseIgnoringInputStream closeIgnoringInputStream = new CloseIgnoringInputStream(inputStream);
    closeIgnoringInputStream.close();
    verify(inputStream, never()).close();
  }
}
