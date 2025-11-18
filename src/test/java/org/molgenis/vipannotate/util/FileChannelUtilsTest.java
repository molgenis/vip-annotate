package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class FileChannelUtilsTest {

  @Test
  void force() throws Exception {
    FileChannel fileChannel = mock(FileChannel.class);
    FileChannelUtils.force(fileChannel);
    verify(fileChannel).force(false);
  }

  @Test
  void forceThrows() throws Exception {
    FileChannel fileChannel = mock(FileChannel.class);
    doThrow(IOException.class).when(fileChannel).force(false);
    assertThrows(UncheckedIOException.class, () -> FileChannelUtils.force(fileChannel));
  }

  @Test
  void forceAll() {
    FileChannel fileChannel0 = mock(FileChannel.class);
    FileChannel fileChannel1 = mock(FileChannel.class);
    FileChannelUtils.forceAll(fileChannel0, fileChannel1);
    assertAll(() -> verify(fileChannel0).force(false), () -> verify(fileChannel1).force(false));
  }

  @Test
  void forceAllThrows() throws IOException {
    FileChannel fileChannel0 = mock(FileChannel.class);

    FileChannel fileChannel1 = mock(FileChannel.class);
    RuntimeException runtimeException = mock(RuntimeException.class);
    doThrow(runtimeException).when(fileChannel1).force(false);

    FileChannel fileChannel2 = mock(FileChannel.class);
    Error error = mock(Error.class);
    doThrow(error).when(fileChannel2).force(false);

    FileChannel fileChannel3 = mock(FileChannel.class);

    try {
      FileChannelUtils.forceAll(fileChannel0, fileChannel1, fileChannel2, fileChannel3);
    } catch (Throwable t) {
      assertAll(
          () -> assertEquals(runtimeException, t),
          () -> verify(runtimeException).addSuppressed(error),
          () -> verify(fileChannel0).force(false),
          () -> verify(fileChannel1).force(false),
          () -> verify(fileChannel2).force(false),
          () -> verify(fileChannel3).force(false));
    }
  }
}
