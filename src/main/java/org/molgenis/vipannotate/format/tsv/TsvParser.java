package org.molgenis.vipannotate.format.tsv;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class TsvParser implements RecordReader<TsvField, TsvRecord> {
  private final TsvRecordReader recordReader;

  @Override
  public @Nullable TsvRecord read() {
    return recordReader.read();
  }

  @Override
  public boolean readInto(TsvRecord record) {
    return recordReader.readInto(record);
  }

  @Override
  public void close() {
    ClosableUtils.close(recordReader);
  }
}
