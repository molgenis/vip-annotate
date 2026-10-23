package org.molgenis.vipannotate.format.vcf;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.StringView;

public enum VcfRecordDummyFactory {
  INSTANCE;

  private static final String DATA_LINE_DUMMY = "1\t0\t.\tA\t.\t.\t.\t.\t.\t.";

  public VcfRecord createDummy() {
    return create(null);
  }

  public VcfRecord createDummyWithGenotypeFields() {
    return create(Genotype.wrap(new StringView(DATA_LINE_DUMMY, 16)));
  }

  private VcfRecord create(@Nullable Genotype genotype) {
    return new VcfRecord(
        Chrom.wrap(new StringView(DATA_LINE_DUMMY, 0, 1)),
        Pos.wrap(new StringView(DATA_LINE_DUMMY, 2, 3)),
        Id.wrap(new StringView(DATA_LINE_DUMMY, 4, 5)),
        Ref.wrap(new StringView(DATA_LINE_DUMMY, 6, 7)),
        Alt.wrap(new StringView(DATA_LINE_DUMMY, 8, 9)),
        Qual.wrap(new StringView(DATA_LINE_DUMMY, 10, 11)),
        Filter.wrap(new StringView(DATA_LINE_DUMMY, 12, 13)),
        Info.wrap(new StringView(DATA_LINE_DUMMY, 14, 15)),
        genotype);
  }
}
