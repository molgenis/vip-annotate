package org.molgenis.vcf.annotate.db.model;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.*;
import lombok.experimental.PackagePrivate;
import lombok.experimental.SuperBuilder;

/**
 * An RNA synthesized on a DNA or RNA template by an RNA polymerase.
 *
 * @see <a href="http://sequenceontology.org/browser/current_release/term/SO:0000673">SO:0000673</a>
 */
@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Transcript extends ClosedInterval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull String id;
  @NonNull Type type;
  @PackagePrivate int geneIndex;
  @NonNull Exon[] exons;
  Cds cds;

  @Getter
  public enum Type {
    PRIMARY_TRANSCRIPT("primary_transcript"),
    R_RNA("rRNA"),
    ANTISENSE_RNA("antisense_RNA"),
    RNASE_MRP_RNA("RNase_MRP_RNA"),
    VAULT_RNA("vault_RNA"),
    M_RNA("mRNA"),
    NC_RNA("ncRNA"),
    SN_RNA("snRNA"),
    Y_RNA("Y_RNA"),
    SNO_RNA("snoRNA"),
    LNC_RNA("lnc_RNA"),
    RNASE_P_RNA("RNase_P_RNA"),
    TRANSCRIPT("transcript"),
    SC_RNA("scRNA"),
    TELOMERASE_RNA("telomerase_RNA");

    private static final Map<String, Type> TERM_TO_TYPE_MAP;

    static {
      TERM_TO_TYPE_MAP = HashMap.newHashMap(Type.values().length);
      for (Type type : Type.values()) {
        TERM_TO_TYPE_MAP.put(type.getTerm(), type);
      }
    }

    private final String term;

    Type(String term) {
      this.term = requireNonNull(term);
    }

    public static Type from(String term) {
      return TERM_TO_TYPE_MAP.get(requireNonNull(term));
    }
  }
}
