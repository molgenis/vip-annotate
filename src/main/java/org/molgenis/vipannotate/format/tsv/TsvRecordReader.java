package org.molgenis.vipannotate.format.tsv;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.util.BufferedLineReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public final class TsvRecordReader implements RecordReader<TsvField, TsvRecord> {
  private static final int BUFFER_SIZE_STRING_BUILDER = 256;

  private final BufferedLineReader reader;
  private final StringBuilder lineBuffer = new StringBuilder(BUFFER_SIZE_STRING_BUILDER);
  private boolean eof;

  @Override
  public @Nullable TsvRecord read() {
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
        return new TsvRecord(lineBuffer);
      }
    }
  }

  @Override
  public boolean readInto(TsvRecord record) {
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
        record.reset(lineBuffer);
        return true;
      }
    }
  }

  @Override
  public void close() {
    ClosableUtils.close(reader);
  }
}
