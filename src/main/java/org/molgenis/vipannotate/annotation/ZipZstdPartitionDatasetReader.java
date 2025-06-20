package org.molgenis.vipannotate.annotation;

import java.nio.ByteBuffer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.format.zip.ZipZstdDecompressionContext;

@RequiredArgsConstructor
public class ZipZstdPartitionDatasetReader implements PartitionDatasetReader {
  @NonNull private final ZipZstdDecompressionContext zipZstdDecompressionContext;

  @Override
  public MemoryBuffer read(
      Partition.Key partitionKey, String datasetId, ByteBuffer directByteBuffer) {
    String zipArchiveEntryName =
        partitionKey.contig().getName() + "/" + partitionKey.bin() + "/" + datasetId + ".zst";
    return zipZstdDecompressionContext.read(zipArchiveEntryName, directByteBuffer);
  }
}
