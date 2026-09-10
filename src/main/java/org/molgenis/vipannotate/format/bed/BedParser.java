package org.molgenis.vipannotate.format.bed;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.util.ClosableUtils;

/** <a href="https://samtools.github.io/hts-specs/BEDv1.pdf">.bed</a> file parser */
@RequiredArgsConstructor
public class BedParser implements RecordReader<BedField, BedFeature> {
  private final BedRecordReader recordReader;

  @Override
  public @Nullable BedFeature read() {
    return recordReader.read();
  }

  @Override
  public boolean readInto(BedFeature bedFeature) {
    return recordReader.readInto(bedFeature);
  }

  @Override
  public void close() {
    ClosableUtils.close(recordReader);
  }
}
