package org.molgenis.vipannotate.format.zip;

import com.github.luben.zstd.Zstd;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor
public class ZipZstdCompressionContext {
  @NonNull private final ZipArchiveOutputStream zipOutputStream;

  public void write(String zipArchiveEntryName, MemoryBuffer memoryBuffer) {

    // use getBytes() instead of getArray() since the backing array might have a size other than
    // size().
    byte[] uncompressedBytes = memoryBuffer.getBytes(0, memoryBuffer.size());
    byte[] compressedBytes = Zstd.compress(uncompressedBytes, 19);

    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipArchiveEntryName);
    //noinspection MagicConstant
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(memoryBuffer.size());

    Logger.info("creating zip archive entry %s", zipArchiveEntry.getName());
    try {
      zipOutputStream.putArchiveEntry(zipArchiveEntry);
      zipOutputStream.write(compressedBytes);
      zipOutputStream.closeArchiveEntry();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
