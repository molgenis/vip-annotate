package org.molgenis.vipannotate.format.vcf;

import java.util.Map;
import org.jspecify.annotations.Nullable;

public record VcfRecord(
    String chrom,
    int pos, // TODO validate constraint: disallowed values: −2^31 to −2^31 + 7
    String[] id,
    String ref,
    String[] alt,
    @Nullable String qual,
    String[] filter,
    Map<String, String> info,
    @Nullable String[] format,
    @Nullable String[] sampleData) {
  public VcfRecord {
    if (pos < 0) throw new IllegalArgumentException("Position is negative");
  }
}
