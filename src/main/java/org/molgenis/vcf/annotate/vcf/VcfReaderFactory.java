package org.molgenis.vcf.annotate.vcf;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.molgenis.vcf.annotate.util.CloseIgnoringInputStream;

public class VcfReaderFactory {
  private VcfReaderFactory() {}

  public static VcfReader create(Path inputVcfPath) {
    InputStream inputStream;
    if (inputVcfPath != null) {
      try {
        inputStream = Files.newInputStream(inputVcfPath);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      inputStream = new CloseIgnoringInputStream(System.in);
    }

    return create(inputStream);
  }

  public static VcfReader create(InputStream inputStream) {
    final int bufferedReaderBufferSize = 32768; // see BgzipDecompressBenchmark
    final int inputStreamReaderBufferSize = 32768;

    BufferedReader bufferedReader;
    try {
      bufferedReader =
          new BufferedReader(
              new InputStreamReader(new GZIPInputStream(inputStream, inputStreamReaderBufferSize)),
              bufferedReaderBufferSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    VcfHeader vcfHeader = VcfHeader.create(bufferedReader);
    VcfRecordIterator vcfRecordIterator = new VcfRecordIterator(bufferedReader);
    return new VcfReader(vcfHeader, vcfRecordIterator);
  }
}
