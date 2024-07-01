package org.molgenis.vipannotate.format.zip;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.zstd.ZstdCompressionContext;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class ZipZstdCompressionContextTest {
  @Mock private ZipArchiveOutputStream zipOutputStream;
  @Mock private ZstdCompressionContext zstdCompressionContext;
  private ZipZstdCompressionContext zipZstdCompressionContext;

  @BeforeEach
  void setUp() {
    zipZstdCompressionContext =
        new ZipZstdCompressionContext(zipOutputStream, zstdCompressionContext);
  }

  @Test
  void write() throws IOException {
    int nrUncompressedBytes = 1024;
    byte[] byteArray = new byte[nrUncompressedBytes];
    MemorySegment srcMemorySegment = MemorySegment.ofArray(byteArray);
    when(zstdCompressionContext.compressBound(srcMemorySegment)).thenReturn(10L);
    when(zstdCompressionContext.compress(any(MemorySegment.class), eq(srcMemorySegment)))
        .thenReturn(5L);
    zipZstdCompressionContext.write("test", srcMemorySegment);

    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry("test");
    zipArchiveEntry.setTime(315532200000L);
    //noinspection MagicConstant
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(nrUncompressedBytes);

    InOrder inOrder = inOrder(zipOutputStream);
    inOrder.verify(zipOutputStream).putArchiveEntry(any());
    inOrder.verify(zipOutputStream).write(new byte[10], 0, 5);
    inOrder.verify(zipOutputStream).closeArchiveEntry();
  }
}
