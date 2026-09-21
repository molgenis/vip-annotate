package org.molgenis.vipannotate.format.tsv;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.molgenis.vipannotate.util.BufferedLineReader;
import org.molgenis.vipannotate.util.CloseIgnoringInputStream;
import org.molgenis.vipannotate.util.Input;

public class TsvParserFactory {
  public enum InputType {
    COMPRESSED,
    UNCOMPRESSED
  }

  private TsvParserFactory() {}

  public static TsvParser create(Input inputTsv) {
    Path inputTsvPath = inputTsv.path();
    InputType inputType;
    InputStream inputStream;
    if (inputTsvPath != null) {
      Path pathFileName = inputTsvPath.getFileName();
      if (pathFileName == null) {
        throw new IllegalArgumentException(
            "Input tsv file path '%s' must not have zero elements".formatted(inputTsvPath));
      }
      String inputTsvFilename = pathFileName.toString();
      inputType =
          inputTsvFilename.endsWith(".gz") || inputTsvFilename.endsWith(".bgz")
              ? InputType.COMPRESSED
              : InputType.UNCOMPRESSED;
      try {
        inputStream = Files.newInputStream(inputTsvPath);
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
          throw new TsvParserException("tsv file is not a valid gzip file");
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

  private static TsvParser create(InputStream inputStream, InputType inputType) {
    BufferedLineReader reader = createReader(inputStream, inputType);
    return new TsvParser(new TsvRecordReader(reader));
  }

  private static BufferedLineReader createReader(
      InputStream inputStream, TsvParserFactory.InputType inputType) {
    final int inputStreamReaderBufferSize = 32768;

    BufferedLineReader reader;
    try {
      InputStream wrappedInputStream =
          switch (inputType) {
            case COMPRESSED -> new GZIPInputStream(inputStream, inputStreamReaderBufferSize);
            case UNCOMPRESSED -> inputStream;
          };
      reader = new BufferedLineReader(new InputStreamReader(wrappedInputStream, UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return reader;
  }
}
