package org.molgenis.vipannotate.format.zip;

import com.github.luben.zstd.ZstdDecompressCtx;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.fury.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class ZipZstdDecompressionContext {
  private final MappableZipFile zipFile;
  private final ZstdDecompressCtx zstdDecompressCtx;

  public @Nullable MemoryBuffer read(String zipArchiveEntryName, ByteBuffer directByteBuffer) {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(zipArchiveEntryName);
    if (zipArchiveEntry == null) {
      return null;
    }

    int compressedSize = Math.toIntExact(zipArchiveEntry.getCompressedSize());
    int uncompressedSize = Math.toIntExact(zipArchiveEntry.getSize());

    directByteBuffer.clear();
    ByteBuffer srcByteBuffer = this.zipFile.map(zipArchiveEntry);
    zstdDecompressCtx.decompressDirectByteBuffer(
        directByteBuffer, 0, uncompressedSize, srcByteBuffer, 0, compressedSize);
    //noinspection UnusedAssignment
    srcByteBuffer = null; // make available for deallocation

    directByteBuffer.position(0);
    directByteBuffer.limit(uncompressedSize);

    return MemoryBuffer.fromDirectByteBuffer(directByteBuffer, uncompressedSize, null);
  }
}
