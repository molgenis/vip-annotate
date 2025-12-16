package org.molgenis.vipannotate.util;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public class Gzip {
  private Gzip() {}

  /**
   * Create {@link BufferedReader} for a gzip input
   *
   * @param input input from file or standard input stream
   */
  public static BufferedReader createBufferedReaderUtf8FromGzip(Input input) {
    final int inputStreamReaderBufferSize = 32768;
    try {
      return Readers.newBufferedReaderUtf8(
          new GZIPInputStream(createInputStream(input), inputStreamReaderBufferSize));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Create {@link InputStream} for an input
   *
   * @param input input from file or standard input stream
   */
  private static InputStream createInputStream(Input input) {
    Path filePath = input.path();
    InputStream inputStream;
    if (filePath == null) {
      inputStream = new CloseIgnoringInputStream(System.in);
    } else {
      try {
        inputStream = new FileInputStream(filePath.toFile());
      } catch (FileNotFoundException e) {
        throw new UncheckedIOException(e);
      }
    }
    return inputStream;
  }
}
