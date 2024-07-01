package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.zip.ZipZstdDecompressionContext;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class ZipZstdPartitionDatasetReader implements PartitionDatasetReader {
  private final ZipZstdDecompressionContext zipZstdDecompressionContext;

  @Override
  public @Nullable MemoryBuffer read(
      PartitionKey partitionKey, String datasetId, MemorySegment dstMemorySegment) {
    String zipArchiveEntryName =
        partitionKey.contig().getName() + "/" + partitionKey.bin() + "/" + datasetId + ".zst";
    MemorySegment memorySegment =
        zipZstdDecompressionContext.read(zipArchiveEntryName, dstMemorySegment);
    return memorySegment != null ? MemoryBuffer.wrap(memorySegment) : null;
  }
}
