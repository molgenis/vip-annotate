package org.molgenis.vcf.annotate.db.chrpos;

/**
 * Based on <a href="https://doi.org/10.1093/nar/gkac931">Echtvar: compressed variant representation
 * for rapid annotation and filtering of SNPs and indels</a>.
 */
public class ContigPosEncoder {
  private static final int NR_ENCODED_PARTITION_ID_BITS = 20;

  public ContigPosEncoder() {}

  public int getPartitionId(ContigPosAnnotation contigPosAnnotation) {
    return contigPosAnnotation.pos() >> NR_ENCODED_PARTITION_ID_BITS;
  }
}
