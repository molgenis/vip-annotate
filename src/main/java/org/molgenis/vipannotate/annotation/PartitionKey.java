package org.molgenis.vipannotate.annotation;

/**
 * Annotated feature partition key
 *
 * @param contig contig
 * @param bin bin index
 */
// TODO move to org.molgenis.vipannotate.format.vdb and refactor
public record PartitionKey(Contig contig, int bin) {

  public PartitionKey getIndexPartitionKey() {
    return new PartitionKey(contig, -1);
  }

  public String getCanonicalName() {
    if (bin == -1) {
      return contig.getName();
    } else {
      return contig.getName() + "/" + bin;
    }
  }

  public String getCanonicalNameForData(String dataId) {
    if (bin == -1) {
      throw new IllegalArgumentException();
    }
    return bin + "/" + dataId;
  }
}
