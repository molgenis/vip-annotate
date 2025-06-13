package org.molgenis.vipannotate.db.v2;

import static org.molgenis.vipannotate.util.ParameterValidation.*;

import lombok.NonNull;

public record GenomePartitionKey(@NonNull String contig, int bin) {
  public GenomePartitionKey {
    requireNonNegative(bin);
  }

  public boolean contigEquals(GenomePartitionKey otherGenomePartitionKey) {
    return otherGenomePartitionKey != null && contig().equals(otherGenomePartitionKey.contig());
  }

  public boolean binEquals(GenomePartitionKey otherGenomePartitionKey) {
    return otherGenomePartitionKey != null && bin() == otherGenomePartitionKey.bin();
  }
}
