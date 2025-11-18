package org.molgenis.vipannotate.util;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

/**
 * Fast alternative for {@link java.io.BufferedReader} that reads into {@link StringBuilder} instead
 * of creating a new {@link String} for each line read.
 */
public final class BufferedLineReader implements AutoCloseable {
  private static final int BUFFER_SIZE_DEFAULT = 32768;

  private final Reader reader;
  private final char[] buffer;
  private int pos = 0, limit = 0;
  private boolean eof = false;

  public BufferedLineReader(Reader reader) {
    this(reader, BUFFER_SIZE_DEFAULT);
  }

  public BufferedLineReader(Reader reader, int bufferSize) {
    this.reader = reader;
    this.buffer = new char[bufferSize];
  }

  /**
   * Reads a line into the given {@link StringBuilder}, excluding {@code LF} and {@code CRLF} line
   * terminators.
   *
   * @return {@code -1} if the end of the stream has been reached and no data was read.
   */
  public int readLineInto(StringBuilder sb) {
    final int sbOffset = sb.length();
    if (eof) {
      return -1;
    }

    try {
      for (; ; ) {
        // refill buffer if empty
        if (pos >= limit) {
          limit = reader.read(buffer);
          pos = 0;
          if (limit == -1) {
            eof = true;
            return sb.length() == sbOffset ? -1 : sb.length() - sbOffset;
          }
        }

        // scan for LF
        int start = pos;
        while (pos < limit && buffer[pos] != '\n') pos++;

        // append chunk to string builder
        sb.append(buffer, start, pos - start);

        if (pos < limit) { // LF found
          pos++; // skip LF

          // trim trailing CR in case of CRLF
          int sbEnd = sb.length();
          if (sbEnd > sbOffset && sb.charAt(sbEnd - 1) == '\r') {
            sb.setLength(sbEnd - 1);
          }

          return sb.length() - sbOffset;
        }
        // no LF yet, continue reading next buffer chunk
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    ClosableUtils.close(reader);
  }
}
