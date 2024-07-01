package org.molgenis.vipannotate.format.vcf;

import static org.molgenis.vipannotate.format.vcf.VcfRecord.*;

public enum VcfRecordDummyFactory {
  INSTANCE;

  private static final String DATA_LINE_DUMMY = "1\t0\t.\tA\t.\t.\t.\t.\t.\t.";

  public VcfRecord createDummy() {
    Field[] fields = new Field[8];
    populateDummyFixedFields(fields);
    return new VcfRecord(fields);
  }

  public VcfRecord createDummyWithGenotypeFields() {
    Field[] fields = new Field[9];
    populateDummyFixedFields(fields);
    fields[INDEX_GENOTYPE] = Genotype.wrap(new StringView(DATA_LINE_DUMMY, 16));
    return new VcfRecord(fields);
  }

  private void populateDummyFixedFields(Field[] fields) {
    fields[INDEX_CHROM] = Chrom.wrap(new StringView(DATA_LINE_DUMMY, 0, 1));
    fields[INDEX_POS] = Pos.wrap(new StringView(DATA_LINE_DUMMY, 2, 3));
    fields[INDEX_ID] = Id.wrap(new StringView(DATA_LINE_DUMMY, 4, 5));
    fields[INDEX_REF] = Ref.wrap(new StringView(DATA_LINE_DUMMY, 6, 7));
    fields[INDEX_ALT] = Alt.wrap(new StringView(DATA_LINE_DUMMY, 8, 9));
    fields[INDEX_QUAL] = Qual.wrap(new StringView(DATA_LINE_DUMMY, 10, 11));
    fields[INDEX_FILTER] = Filter.wrap(new StringView(DATA_LINE_DUMMY, 12, 13));
    fields[INDEX_INFO] = Info.wrap(new StringView(DATA_LINE_DUMMY, 14, 15));
  }
}
