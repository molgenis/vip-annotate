package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;

@RequiredArgsConstructor
public class ZipZstdBinaryPartitionWriter implements BinaryPartitionWriter {
  private final ZipZstdCompressionContext zipZstdCompressionContext;

  @Override
  public void write(Partition.Key partitionKey, String dataId, MemoryBuffer memoryBuffer) {
    String zipArchiveEntryName =
        partitionKey.contig().getName() + "/" + partitionKey.bin() + "/" + dataId + ".zst";
    zipZstdCompressionContext.write(zipArchiveEntryName, memoryBuffer);
  }
}
