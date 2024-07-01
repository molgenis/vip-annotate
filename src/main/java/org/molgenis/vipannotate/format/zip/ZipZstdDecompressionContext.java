package org.molgenis.vipannotate.format.zip;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.jspecify.annotations.Nullable;
import org.molgenis.zstd.ZstdDecompressionContext;

@RequiredArgsConstructor
public class ZipZstdDecompressionContext {
  private final MappableZipFile zipFile;
  private final ZstdDecompressionContext zstdDecompressionContext;

  public @Nullable MemorySegment read(String zipArchiveEntryName, MemorySegment dstMemorySegment) {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(zipArchiveEntryName);
    if (zipArchiveEntry == null) {
      return null;
    }

    long uncompressedSize;
    try (Arena arena = Arena.ofConfined()) {
      @SuppressWarnings("DataFlowIssue")
      MemorySegment srcMemorySegment = this.zipFile.map(zipArchiveEntry, arena);
      uncompressedSize = zstdDecompressionContext.decompress(dstMemorySegment, srcMemorySegment);
    }

    return dstMemorySegment.asSlice(0L, uncompressedSize);
  }
}
