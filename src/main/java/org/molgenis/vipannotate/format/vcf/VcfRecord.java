package org.molgenis.vipannotate.format.vcf;

import java.util.Map;
import org.jspecify.annotations.Nullable;

public record VcfRecord(
    String chrom,
    int pos, // TODO validate constraint: disallowed values: −2^31 to −2^31 + 7
    String[] id,
    String ref,
    @Nullable String[] alt,
    @Nullable String qual,
    String[] filter,
    Map<String, @Nullable String> info,
    String @Nullable [] format,
    String @Nullable [] sampleData) {
  public VcfRecord {
    if (pos < 0) throw new IllegalArgumentException("Position is negative");
  }
}
