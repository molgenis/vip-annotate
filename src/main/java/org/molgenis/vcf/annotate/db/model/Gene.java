package org.molgenis.vcf.annotate.db.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * A region (or regions) that includes all of the sequence elements necessary to encode a functional
 * transcript. A gene may include regulatory regions, transcribed regions and/or other functional
 * sequence regions.
 *
 * @see <a
 *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000704">SO:0000704</a>
 */
@Value
@Builder
public class Gene {
  int id;
  @NonNull String name;
  @NonNull Gene.BioType bioType;
  @NonNull Strand strand;

  @Getter
  public enum BioType {
    ANTISENSE_RNA("antisense_RNA"),
    C_REGION("C_region"),
    C_REGION_PSEUDOGENE("C_region_pseudogene"),
    D_SEGMENT("D_segment"),
    J_SEGMENT("J_segment"),
    J_SEGMENT_PSEUDOGENE("J_segment_pseudogene"),
    LNC_RNA("lncRNA"),
    MI_RNA("miRNA"),
    MISC_RNA("misc_RNA"),
    NC_RNA("ncRNA"),
    NC_RNA_PSEUDOGENE("ncRNA_pseudogene"),
    OTHER("other"),
    PROTEIN_CODING("protein_coding"),
    PSEUDOGENE("pseudogene"),
    R_RNA("rRNA"),
    RNASE_MRP_RNA("RNase_MRP_RNA"),
    RNase_P_RNA("RNase_P_RNA"),
    SC_RNA("scRNA"),
    SN_RNA("snRNA"),
    SNO_RNA("snoRNA"),
    T_RNA("tRNA"),
    TELOMERASE_RNA("telomerase_RNA"),
    TRANSCRIBED_PSEUDOGENE("transcribed_pseudogene"),
    V_SEGMENT("V_segment"),
    V_SEGMENT_PSEUDOGENE("V_segment_pseudogene"),
    VAULT_RNA("vault_RNA"),
    Y_RNA("Y_RNA");

    private static final Map<String, BioType> TERM_TO_BIOTYPE_MAP;

    static {
      TERM_TO_BIOTYPE_MAP = HashMap.newHashMap(BioType.values().length);
      for (BioType bioType : BioType.values()) {
        TERM_TO_BIOTYPE_MAP.put(bioType.getTerm(), bioType);
      }
    }

    private final String term;

    BioType(String term) {
      this.term = requireNonNull(term);
    }

    public static BioType from(String term) {
      return TERM_TO_BIOTYPE_MAP.get(requireNonNull(term));
    }
  }
}
