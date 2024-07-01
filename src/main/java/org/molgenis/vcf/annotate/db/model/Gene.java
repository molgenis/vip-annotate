package org.molgenis.vcf.annotate.db.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Gene {
  @NonNull String name;
  @NonNull Gene.Type type;
  @NonNull Strand strand;

  public enum Type {
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
