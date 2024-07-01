package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class ZipZstdBinaryPartitionWriter implements BinaryPartitionWriter {
  private final ZipZstdCompressionContext zipZstdCompressionContext;

  @Override
  public void write(PartitionKey partitionKey, String dataId, MemoryBuffer memoryBuffer) {
    String zipArchiveEntryName =
        partitionKey.contig().getName() + "/" + partitionKey.bin() + "/" + dataId + ".zst";
    zipZstdCompressionContext.write(zipArchiveEntryName, memoryBuffer.asMemorySegment());
  }
}
