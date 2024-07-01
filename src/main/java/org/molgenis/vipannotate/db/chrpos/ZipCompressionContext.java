package org.molgenis.vipannotate.db.chrpos;

import com.github.luben.zstd.Zstd;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.molgenis.vipannotate.util.Logger;

public class ZipCompressionContext {

  public void writeData(
      String zipArchiveEntryName,
      byte[] uncompressedBytes,
      ZipArchiveOutputStream zipOutputStream) {
    // do not use ultra 20-22 levels because https://github.com/facebook/zstd/issues/435
    byte[] compressesBytes = Zstd.compress(uncompressedBytes, 19);

    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipArchiveEntryName);
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(uncompressedBytes.length);
    write(compressesBytes, zipArchiveEntry, zipOutputStream);
  }

  private void write(
      byte[] compressesBytes,
      ZipArchiveEntry zipArchiveEntry,
      ZipArchiveOutputStream zipOutputStream) {
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
