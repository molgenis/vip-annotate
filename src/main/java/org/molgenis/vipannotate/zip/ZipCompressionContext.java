package org.molgenis.vipannotate.zip;

import com.github.luben.zstd.Zstd;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.molgenis.vipannotate.util.Logger;

public class ZipCompressionContext {
  private final Map<String, byte[]> contigZstdDictionaryMap;
  private final Set<String> contigWrittenSet;

  public ZipCompressionContext() {
    this(null);
  }

  public ZipCompressionContext(Map<String, byte[]> contigZstdDictionaryMap) {
    this.contigZstdDictionaryMap = contigZstdDictionaryMap;
    this.contigWrittenSet = new HashSet<>();
  }

  public void writeData(
      String contig,
      int partitionId,
      byte[] uncompressedBytes,
      ZipArchiveOutputStream zipOutputStream) {
    if (contigZstdDictionaryMap != null && !contigWrittenSet.contains(contig)) {
      writeDataDictionary(contig, zipOutputStream);
      contigWrittenSet.add(contig);
    }

    // do not use ultra 20-22 levels because https://github.com/facebook/zstd/issues/435
    byte[] compressesBytes =
        Zstd.compressUsingDict(uncompressedBytes, contigZstdDictionaryMap.get(contig), 19);

    String zipArchiveEntryName = contig + "/var/" + partitionId + ".zst";
    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipArchiveEntryName);
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(uncompressedBytes.length);
    write(compressesBytes, zipArchiveEntry, zipOutputStream);
  }

  private void writeDataDictionary(String contig, ZipArchiveOutputStream zipOutputStream) {
    byte[] zstdDictionary = contigZstdDictionaryMap.get(contig);
    String zipArchiveEntryName = contig + "/var/zst.dict";
    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipArchiveEntryName);
    // java.util.zip.ZipException: CRC checksum is required for STORED method when not writing to a
    // file
    zipArchiveEntry.setMethod(ZipMethod.ZSTD.getCode());
    zipArchiveEntry.setSize(zstdDictionary.length);
    write(zstdDictionary, zipArchiveEntry, zipOutputStream);
  }

  public void writeDataIndex(
      String contig,
      int partitionId,
      byte[] uncompressedBytes,
      ZipArchiveOutputStream zipOutputStream) {
    byte[] compressesBytes = Zstd.compress(uncompressedBytes, 19);

    String zipArchiveEntryName = contig + "/var/" + partitionId + ".idx.zst";
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
