package org.molgenis.vipannotate.zip;

import com.github.luben.zstd.Zstd;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.GenomePartitionKey;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor
public class ZipZstdCompressionContext {
  @NonNull private final ZipArchiveOutputStream zipOutputStream;

  public void write(
      GenomePartitionKey genomePartitionKey, String basename, MemoryBuffer memoryBuffer) {

    byte[] compressesBytes = Zstd.compress(memoryBuffer.getArray(), 19);

    String zipArchiveEntryName =
        genomePartitionKey.contig() + "/" + genomePartitionKey.bin() + "/" + basename + ".zst";
    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipArchiveEntryName);
    //noinspection MagicConstant
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(memoryBuffer.size());

    Logger.info("creating zip archive entry %s", zipArchiveEntry.getName());
    try {
      zipOutputStream.putArchiveEntry(zipArchiveEntry);
      zipOutputStream.write(compressesBytes);
      zipOutputStream.closeArchiveEntry();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
