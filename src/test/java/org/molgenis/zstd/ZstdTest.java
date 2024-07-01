package org.molgenis.zstd;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class ZstdTest {
  private static Zstd zstd;

  @BeforeAll
  static void setUp() {
    zstd = Zstd.create();
  }

  @AfterAll
  static void tearDown() {
    zstd.close();
  }

  @Test
  void compressAndDecompress() {
    byte[] byteArray = new byte[1024];
    for (int i = 0; i < byteArray.length; i++) {
      byteArray[i] = (byte) i;
    }
    try (Arena arena = Arena.ofConfined();
        ZstdCompressionContext compressionContext = zstd.createCompressionContext();
        ZstdDecompressionContext decompressionContext = zstd.createDecompressionContext()) {
      MemorySegment srcMemorySegment = arena.allocateFrom(ValueLayout.JAVA_BYTE, byteArray);

      // compress
      long maxCompressedBytes = compressionContext.compressBound(srcMemorySegment);
      MemorySegment dstMemorySegment = arena.allocate(maxCompressedBytes);
      long nrCompressedBytes = compressionContext.compress(dstMemorySegment, srcMemorySegment);
      MemorySegment compressedMemorySegment = dstMemorySegment.asSlice(0, nrCompressedBytes);
      MemorySegment decompressedMemorySegment = arena.allocate(byteArray.length);

      // decompress
      long nrDecompressedBytes =
          decompressionContext.decompress(decompressedMemorySegment, compressedMemorySegment);
      assertAll(
          () -> assertEquals(byteArray.length, nrDecompressedBytes),
          () ->
              assertArrayEquals(
                  byteArray,
                  decompressedMemorySegment
                      .asSlice(0, nrDecompressedBytes)
                      .toArray(ValueLayout.JAVA_BYTE)));
    }
  }
}
