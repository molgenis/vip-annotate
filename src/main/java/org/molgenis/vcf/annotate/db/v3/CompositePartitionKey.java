package org.molgenis.vcf.annotate.db.v3;

import lombok.NonNull;

public record CompositePartitionKey<KeyType>(@NonNull KeyType... keyName) {
  @SafeVarargs
  public CompositePartitionKey {}
}
