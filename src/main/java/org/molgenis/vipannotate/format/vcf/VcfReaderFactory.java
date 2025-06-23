package org.molgenis.vipannotate.format.vcf;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.CloseIgnoringInputStream;

public class VcfReaderFactory {
  public enum InputType {
    COMPRESSED,
    UNCOMPRESSED
  }

  private VcfReaderFactory() {}

  public static VcfReader create(@Nullable Path inputVcfPath) {
    InputType inputType;
    InputStream inputStream;
    if (inputVcfPath != null) {
      String inputVcfFilename = inputVcfPath.getFileName().toString();
      inputType =
          inputVcfFilename.endsWith(".gz") || inputVcfFilename.endsWith(".bgz")
              ? InputType.COMPRESSED
              : InputType.UNCOMPRESSED;
      try {
        inputStream = Files.newInputStream(inputVcfPath);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      int pushbackBufferSize = 2;
      PushbackInputStream pushbackInputStream =
          new PushbackInputStream(new CloseIgnoringInputStream(System.in), pushbackBufferSize);

      byte[] buffer = new byte[pushbackBufferSize];
      int bytesRead;
      try {
        bytesRead = pushbackInputStream.read(buffer);
        if (bytesRead != 2) {
          throw new VcfParserException("vcf file is not a valid gzip file");
        }

        // gzip magic number: 1F 8B
        inputType =
            (buffer[0] == ((byte) 0x1F) && buffer[1] == ((byte) 0x8B))
                ? InputType.COMPRESSED
                : InputType.UNCOMPRESSED;

        pushbackInputStream.unread(buffer, 0, bytesRead);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      inputStream = pushbackInputStream;
    }

    return create(inputStream, inputType);
  }

  private static VcfReader create(InputStream inputStream, InputType inputType) {
    BufferedReader bufferedReader = createBufferedReader(inputStream, inputType);

    VcfHeader vcfHeader =
        new VcfHeaderParser(new VcfMetaInfoLineParser(), new VcfHeaderLineParser())
            .parse(bufferedReader);
    VcfRecordIterator vcfRecordIterator = new VcfRecordIterator(bufferedReader);
    return new VcfReader(vcfHeader, vcfRecordIterator);
  }

  private static BufferedReader createBufferedReader(InputStream inputStream, InputType inputType) {
    final int bufferedReaderBufferSize = 32768; // see BgzipDecompressBenchmark
    final int inputStreamReaderBufferSize = 32768;

    BufferedReader bufferedReader;
    try {
      InputStream inputStreamReaderInputStream =
          switch (inputType) {
            case COMPRESSED -> new GZIPInputStream(inputStream, inputStreamReaderBufferSize);
            case UNCOMPRESSED -> inputStream;
          };
      bufferedReader =
          new BufferedReader(
              new InputStreamReader(inputStreamReaderInputStream), bufferedReaderBufferSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return bufferedReader;
  }
}
