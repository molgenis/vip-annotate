package org.molgenis.vipannotate.format.bed;

import static org.molgenis.vipannotate.format.bed.BedFeature.*;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public enum BedFeatureDummyFactory {
  INSTANCE;

  private static final String DATA_LINE_DUMMY = "1\t0\t1\t.";

  public BedFeature createDummy() {
    Field[] fields = new Field[4];
    populateDummyFixedFields(fields);
    fields[INDEX_NAME] = Name.wrap(new StringView(DATA_LINE_DUMMY, 6, 7));
    return new BedFeature(fields);
  }

  private void populateDummyFixedFields(Field[] fields) {
    fields[INDEX_CHROM] = Chrom.wrap(new StringView(DATA_LINE_DUMMY, 0, 1));
    fields[INDEX_CHROM_START] = ChromStart.wrap(new StringView(DATA_LINE_DUMMY, 2, 3));
    fields[INDEX_CHROM_END] = ChromEnd.wrap(new StringView(DATA_LINE_DUMMY, 4, 5));
  }
}
