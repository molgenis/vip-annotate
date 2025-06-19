package org.molgenis.vipannotate.format.vcf;

import java.util.Map;
import lombok.NonNull;

public record VcfRecord(
    @NonNull String chrom,
    int pos, // TODO validate constraint: disallowed values: −2^31 to −2^31 + 7
    @NonNull String[] id,
    @NonNull String ref,
    @NonNull String[] alt,
    String qual,
    @NonNull String[] filter,
    @NonNull Map<String, String> info,
    String[] format,
    String[] sampleData) {
  public VcfRecord {
    if (pos < 0) throw new IllegalArgumentException("Position is negative");
  }
}
