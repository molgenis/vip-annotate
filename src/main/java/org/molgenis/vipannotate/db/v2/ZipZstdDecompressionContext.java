package org.molgenis.vipannotate.db.v2;

import com.github.luben.zstd.ZstdDecompressCtx;
import java.nio.ByteBuffer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.MappableZipFile;

@RequiredArgsConstructor
public class ZipZstdDecompressionContext {
  @NonNull private final MappableZipFile zipFile;
  @NonNull private final ZstdDecompressCtx zstdDecompressCtx;

  public MemoryBuffer read(
      GenomePartitionKey genomePartitionKey, String basename, ByteBuffer directByteBuffer) {
    ZipArchiveEntry zipArchiveEntry =
        zipFile.getEntry(
            genomePartitionKey.contig() + "/" + genomePartitionKey.bin() + "/" + basename + ".zst");
    if (zipArchiveEntry == null) {
      return null;
    }

    int compressedSize = Math.toIntExact(zipArchiveEntry.getCompressedSize());
    int uncompressedSize = Math.toIntExact(zipArchiveEntry.getSize());

    directByteBuffer.clear();
    ByteBuffer srcByteBuffer = this.zipFile.map(zipArchiveEntry);
    zstdDecompressCtx.decompressDirectByteBuffer(
        directByteBuffer, 0, uncompressedSize, srcByteBuffer, 0, compressedSize);
    srcByteBuffer = null; // make available for deallocation

    directByteBuffer.position(0);
    directByteBuffer.limit(uncompressedSize);

    return MemoryBuffer.fromDirectByteBuffer(
        directByteBuffer, Math.toIntExact(zipArchiveEntry.getSize()), null);
  }
}
