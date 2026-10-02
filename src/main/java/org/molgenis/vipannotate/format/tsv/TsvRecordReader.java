package org.molgenis.vipannotate.format.tsv;

import org.molgenis.vipannotate.format.LineRecordReader;
import org.molgenis.vipannotate.util.BufferedLineReader;

public final class TsvRecordReader extends LineRecordReader<TsvField, TsvRecord> {
  private final String missingValue;

  public TsvRecordReader(BufferedLineReader reader, String missingValue) {
    super(reader);
    this.missingValue = missingValue;
  }

  @Override
  protected TsvRecord createRecord(CharSequence line) {
    return new TsvRecord(line, missingValue);
  }

  @Override
  protected void resetRecord(TsvRecord record, CharSequence line) {
    record.reset(line);
  }
}
