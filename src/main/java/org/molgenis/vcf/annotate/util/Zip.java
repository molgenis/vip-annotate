package org.molgenis.vcf.annotate.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class Zip {
  private Zip() {}

  public static BufferedReader createBufferedReaderUtf8FromGzip(Path filePath) {
    if (Files.notExists(filePath)) {
      throw new IllegalArgumentException("File '%s' does not exist".formatted(filePath));
    }

    final int bufferedReaderBufferSize = 32768;
    final int inputStreamReaderBufferSize = 32768;
    try {
      return new BufferedReader(
          new InputStreamReader(
              new GZIPInputStream(
                  new FileInputStream(filePath.toFile()), inputStreamReaderBufferSize),
              StandardCharsets.UTF_8),
          bufferedReaderBufferSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static ZipArchiveOutputStream createZipArchiveOutputStream(Path filePath) {
    final int bufferedOutputStreamBufferSize = 32768;
    try {
      return new ZipArchiveOutputStream(
          new BufferedOutputStream(
              new FileOutputStream(filePath.toFile()), bufferedOutputStreamBufferSize));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
