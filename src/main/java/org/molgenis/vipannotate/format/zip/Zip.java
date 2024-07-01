package org.molgenis.vipannotate.format.zip;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.util.*;

public class Zip {
  private Zip() {}

  /**
   * Create {@link BufferedReader} for a zip input
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
   * Create {@link InputStream} for a zip input
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

  /**
   * Create {@link ZipArchiveOutputStream} for a zip output
   *
   * @param output file or standout output stream output
   */
  public static ZipArchiveOutputStream createZipArchiveOutputStream(Output output) {
    final int bufferedOutputStreamBufferSize = 32768;
    return new ZipArchiveOutputStream(
        new BufferedOutputStream(createOutputStream(output), bufferedOutputStreamBufferSize));
  }

  /**
   * Create {@link OutputStream} for a zip output
   *
   * @param output file or standout output stream output
   */
  private static OutputStream createOutputStream(Output output) {
    Path filePath = output.path();
    OutputStream outputStream;
    if (filePath == null) {
      outputStream = new CloseIgnoringOutputStream(System.out);
    } else {
      try {
        outputStream = new FileOutputStream(filePath.toFile());
      } catch (FileNotFoundException e) {
        throw new UncheckedIOException(e);
      }
    }
    return outputStream;
  }
}
