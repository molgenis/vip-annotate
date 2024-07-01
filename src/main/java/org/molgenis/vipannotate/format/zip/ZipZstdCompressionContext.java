package org.molgenis.vipannotate.format.zip;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.zstd.ZstdCompressionContext;

@RequiredArgsConstructor
public class ZipZstdCompressionContext {
  private final ZipArchiveOutputStream zipOutputStream;
  private final ZstdCompressionContext zstdCompressionContext;

  public void write(String zipArchiveEntryName, MemorySegment srcMemorySegment) {
    long uncompressedSize = srcMemorySegment.byteSize();

    long maxNrCompressedBytes = zstdCompressionContext.compressBound(srcMemorySegment);
    byte[] compressedBytes =
        new byte[Math.toIntExact(maxNrCompressedBytes)]; // TODO perf: reuse buffer
    MemorySegment dstMemorySegment = MemorySegment.ofArray(compressedBytes);

    long nrCompressedBytes = zstdCompressionContext.compress(dstMemorySegment, srcMemorySegment);

    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipArchiveEntryName);
    // set time for reproducible builds, instead of zero use the java time representation of the
    // smallest date/time ZIP can handle, see ZipUtil.java
    zipArchiveEntry.setTime(315532200000L);
    //noinspection MagicConstant
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(uncompressedSize);

    Logger.debug("creating zip archive entry %s", zipArchiveEntry.getName());
    try {
      zipOutputStream.putArchiveEntry(zipArchiveEntry);
      zipOutputStream.write(compressedBytes, 0, Math.toIntExact(nrCompressedBytes));
      zipOutputStream.closeArchiveEntry();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
