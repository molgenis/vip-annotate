package org.molgenis.vipannotate.format.tsv;

import org.molgenis.vipannotate.format.LineRecordReader;
import org.molgenis.vipannotate.util.BufferedLineReader;

public final class TsvRecordReader extends LineRecordReader<TsvField, TsvRecord> {
  public TsvRecordReader(BufferedLineReader reader) {
    super(reader);
  }

  @Override
  protected TsvRecord createRecord(CharSequence line) {
    return new TsvRecord(line);
  }

  @Override
  protected void resetRecord(TsvRecord record, CharSequence line) {
    record.reset(line);
  }
}
