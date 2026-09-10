package org.molgenis.vipannotate.format;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.BufferedLineReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public abstract class LineRecordReader<F extends Field, R extends Record<F>>
    implements RecordReader<F, R> {
  private static final int BUFFER_SIZE_STRING_BUILDER = 256;

  private final BufferedLineReader reader;
  private final StringBuilder lineBuffer = new StringBuilder(BUFFER_SIZE_STRING_BUILDER);
  private boolean eof;

  @Override
  public @Nullable R read() {
    if (eof) {
      return null;
    }

    StringBuilder lineBuffer = new StringBuilder(BUFFER_SIZE_STRING_BUILDER);
    while (true) {
      lineBuffer.setLength(0);

      int nrCharsRead = reader.readLineInto(lineBuffer);
      if (nrCharsRead == -1) {
        eof = true;
        return null;
      }

      if (!lineBuffer.isEmpty() && lineBuffer.charAt(0) != '#') {
        return createRecord(lineBuffer);
      }
    }
  }

  @Override
  public boolean readInto(R record) {
    if (eof) {
      return false;
    }

    while (true) {
      lineBuffer.setLength(0);

      int nrCharsRead = reader.readLineInto(lineBuffer);
      if (nrCharsRead == -1) {
        eof = true;
        return false;
      }

      if (!lineBuffer.isEmpty() && lineBuffer.charAt(0) != '#') {
        resetRecord(record, lineBuffer);
        return true;
      }
    }
  }

  protected abstract R createRecord(CharSequence line);

  protected abstract void resetRecord(R record, CharSequence line);

  @Override
  public void close() {
    ClosableUtils.close(reader);
  }
}
