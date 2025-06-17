package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;

@RequiredArgsConstructor
public class ZipZstdGenomePartitionDataWriter implements GenomePartitionDataWriter {
  @NonNull private final ZipZstdCompressionContext zipZstdCompressionContext;

  @Override
  public void write(
      GenomePartitionKey genomePartitionKey, String dataId, MemoryBuffer memoryBuffer) {
    String zipArchiveEntryName =
        genomePartitionKey.contig() + "/" + genomePartitionKey.bin() + "/" + dataId + ".zst";
    zipZstdCompressionContext.write(zipArchiveEntryName, memoryBuffer);
  }
}
