package org.molgenis.vipannotate.format.tsv;

import org.molgenis.vipannotate.format.LineRecordReader;
import org.molgenis.vipannotate.util.BufferedLineReader;

public final class TsvRecordReader extends LineRecordReader<TsvField, TsvRecord> {
  private final String missingValue;
  private final char listSeparator;

  public TsvRecordReader(BufferedLineReader reader, String missingValue, char listSeparator) {
    super(reader);
    this.missingValue = missingValue;
    this.listSeparator = listSeparator;
  }

  @Override
  protected TsvRecord createRecord(CharSequence line) {
    return new TsvRecord(line, missingValue, listSeparator);
  }

  @Override
  protected void resetRecord(TsvRecord record, CharSequence line) {
    record.reset(line);
  }
}
