package org.molgenis.vipannotate.format.vcf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.TokenSizeExceededException;
import org.molgenis.vipannotate.format.Tokenizer;
import org.molgenis.vipannotate.format.Utf8ByteSlice;

public class VcfTokenizer implements Tokenizer<Utf8ByteSlice> {
  private static final int DEFAULT_BUFFER_SIZE = 65536;

  private final InputStream inputStream;
  private final byte[] buffer;
  private int bufferStart;
  private int bufferEnd;
  @Nullable private final Utf8ByteSlice reusableToken;

  public VcfTokenizer(InputStream inputStream) {
    this(inputStream, DEFAULT_BUFFER_SIZE);
  }

  public VcfTokenizer(InputStream inputStream, int bufferSize) {
    this(inputStream, bufferSize, false);
  }

  public VcfTokenizer(InputStream inputStream, int bufferSize, boolean reuseToken) {
    this.inputStream = inputStream;
    this.buffer = new byte[bufferSize];
    this.reusableToken = reuseToken ? new Utf8ByteSlice(buffer, 0, bufferSize) : null;
  }

  /**
   * @return vcf tokens excluding tab and including newline
   */
  @Override
  public @Nullable CharSequence nextToken() {
    try {
      return nextTokenChecked();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private @Nullable Utf8ByteSlice nextTokenChecked() throws IOException {
    if (bufferEnd == -1) {
      return null;
    }

    int tokenStart = bufferStart, tokenEnd;

    while (true) {
      if (bufferStart >= bufferEnd) {
        // if a token fills the complete buffer, and the stream could contain more data
        if (bufferStart - tokenStart == buffer.length) {
          throw new TokenSizeExceededException(buffer.length - 1);
        }

        rotateAndFillBuffer(tokenStart);

        if (bufferEnd == -1) {
          tokenEnd = bufferStart; // FIXME see failed test
          break;
        }

        tokenStart = 0;
      }

      byte b = buffer[bufferStart];
      if (b == '\t') {
        tokenEnd = bufferStart;
        bufferStart++; // skip the tab token itself
        break;
      }
      if (b == '\n' || b == '\r') {
        tokenEnd = bufferStart;

        // return newline tokens, one per line separator character
        if (tokenStart == bufferStart) {
          tokenEnd = tokenStart + 1;
          bufferStart++;
        }
        break;
      }

      bufferStart++;
    }

    int length = tokenEnd - tokenStart;
    return length != 0 ? createToken(tokenStart, length) : null;
  }

  private void rotateAndFillBuffer(int rotatePos) throws IOException {
    if (bufferEnd != 0 && bufferEnd < buffer.length) {
      bufferEnd = -1;
      return;
    }
    int remaining = bufferEnd - rotatePos;
    if (rotatePos != 0) {
      System.arraycopy(buffer, rotatePos, buffer, 0, remaining);
    }
    int n = inputStream.read(buffer, remaining, buffer.length - remaining);
    bufferStart = 0;
    bufferEnd = n != -1 ? remaining + n : -1;
  }

  private Utf8ByteSlice createToken(int start, int length) {
    Utf8ByteSlice utf8ByteSlice;
    if (reusableToken != null) {
      reusableToken.setOffset(start);
      reusableToken.setLength(length);
      utf8ByteSlice = reusableToken;
    } else {
      utf8ByteSlice = new Utf8ByteSlice(buffer, start, length);
    }
    return utf8ByteSlice;
  }

  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
