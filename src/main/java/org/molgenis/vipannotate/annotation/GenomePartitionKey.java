package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.*;

import lombok.NonNull;

public record GenomePartitionKey(@NonNull String contig, int bin) {
  public GenomePartitionKey {
    validateNonNegative(bin);
  }
}
