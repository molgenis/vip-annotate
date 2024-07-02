package org.molgenis.vcf.annotate.db.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Gene {
  int id;
  @NonNull String name;
  @NonNull Gene.BioType bioType;
  @NonNull Strand strand;

  /*
    antisense_RNA
  C_region
  C_region_pseudogene
  D_segment
  J_segment
  J_segment_pseudogene
  lncRNA
  miRNA
  misc_RNA
  ncRNA
  ncRNA_pseudogene
  other
  protein_coding
  pseudogene
  RNase_MRP_RNA
  RNase_P_RNA
  rRNA
  scRNA
  snoRNA
  snRNA
  telomerase_RNA
  transcribed_pseudogene
  tRNA
  V_segment
  V_segment_pseudogene
  vault_RNA
  Y_RNA
     */
  public enum BioType {
    ANTISENSE_RNA,
    C_REGION,
    J_SEGMENT,
    LNC_RNA,
    MI_RNA,
    MISC_RNA,
    NC_RNA,
    NC_RNA_PSEUDOGENE,
    PROTEIN_CODING,
    PSEUDOGENE,
    R_RNA,
    RNASE_MRP_RNA,
    SC_RNA,
    SN_RNA,
    SNO_RNA,
    TEC,
    T_RNA,
    TELOMERASE_RNA,
    TRANSCRIBED_PSEUDOGENE,
    V_SEGMENT,
    V_SEGMENT_PSEUDOGENE;
  }
}
