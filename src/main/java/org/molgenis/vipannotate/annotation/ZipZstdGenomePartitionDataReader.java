package org.molgenis.vipannotate.annotation;

import java.nio.ByteBuffer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.format.zip.ZipZstdDecompressionContext;

@RequiredArgsConstructor
public class ZipZstdGenomePartitionDataReader implements GenomePartitionDataReader {
  @NonNull private final ZipZstdDecompressionContext zipZstdDecompressionContext;

  @Override
  public MemoryBuffer read(
      GenomePartitionKey genomePartitionKey, String dataId, ByteBuffer directByteBuffer) {
    String zipArchiveEntryName =
        genomePartitionKey.contig() + "/" + genomePartitionKey.bin() + "/" + dataId + ".zst";
    return zipZstdDecompressionContext.read(zipArchiveEntryName, directByteBuffer);
  }
}
