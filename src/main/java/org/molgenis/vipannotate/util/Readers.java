package org.molgenis.vipannotate.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.nio.file.Path;

public class Readers {
  private static final int BUFFERED_READER_BUFFER_SIZE = 32768;

  private Readers() {}

  public static BufferedReader newBufferedReaderUtf8(Path faiPath) {
    try {
      return newBufferedReaderUtf8(new FileInputStream(faiPath.toFile()));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static BufferedReader newBufferedReaderUtf8(InputStream inputStream) {
    return new BufferedReader(
        new InputStreamReader(inputStream, UTF_8), BUFFERED_READER_BUFFER_SIZE);
  }
}
