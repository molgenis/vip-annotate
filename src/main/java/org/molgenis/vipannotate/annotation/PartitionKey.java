package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

/**
 * Annotated feature partition key
 *
 * @param contig contig
 * @param bin bin index
 */
public record PartitionKey(Contig contig, int bin) {
  public PartitionKey {
    validateNonNegative(bin);
  }
}
