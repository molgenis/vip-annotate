package org.molgenis.vipannotate.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

public class ZeroCopyBufferedWriter extends Writer {
  private static final int DEFAULT_INITIAL_BUFFER_SIZE = 32768;

  private final Writer writer;
  private final CharArrayBuffer charArrayBuffer;

  public ZeroCopyBufferedWriter(Writer writer) {
    this.writer = writer;
    this.charArrayBuffer = new CharArrayBuffer(DEFAULT_INITIAL_BUFFER_SIZE);
  }

  @Override
  public Writer append(char c) {
    this.charArrayBuffer.append(c);
    return this;
  }

  @Override
  public Writer append(CharSequence csq) {
    this.charArrayBuffer.append(csq);
    return this;
  }

  @Override
  public Writer append(CharSequence csq, int start, int end) {
    this.charArrayBuffer.append(csq, start, end);
    return this;
  }

  @Override
  public void write(int c) {
    this.charArrayBuffer.append((char) c);
  }

  @Override
  public void write(char[] cbuf) {
    this.charArrayBuffer.append(cbuf);
  }

  @Override
  public void write(char[] cbuf, int off, int len) {
    this.charArrayBuffer.append(cbuf, off, len);
  }

  @Override
  public void write(String str) {
    this.charArrayBuffer.append(str);
  }

  @Override
  public void write(String s, int off, int len) {
    this.charArrayBuffer.append(s, off, len);
  }

  public void flushBuffer() {
    int length = charArrayBuffer.getLength();
    if (length > 0) {
      try {
        writer.write(charArrayBuffer.getBuffer(), 0, length);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      charArrayBuffer.clear();
    }
  }

  @Override
  public void flush() {
    flushBuffer();
    try {
      writer.flush();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
