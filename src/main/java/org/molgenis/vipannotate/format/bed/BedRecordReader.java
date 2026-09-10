package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.LineRecordReader;
import org.molgenis.vipannotate.util.BufferedLineReader;

public final class BedRecordReader extends LineRecordReader<BedField, BedFeature> {
  public BedRecordReader(BufferedLineReader reader) {
    super(reader);
  }

  @Override
  protected BedFeature createRecord(CharSequence line) {
    return new BedFeature(line);
  }

  @Override
  protected void resetRecord(BedFeature bedFeature, CharSequence line) {
    bedFeature.reset(line);
  }
}
