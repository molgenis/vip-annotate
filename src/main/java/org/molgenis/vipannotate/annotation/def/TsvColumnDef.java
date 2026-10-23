package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

public record TsvColumnDef(
    @JsonProperty(value = "index") @Nullable Integer index,
    @JsonProperty(value = "name") @Nullable String name) {
  public TsvColumnDef {
    if (index == null && name == null) {
      throw new IllegalArgumentException("tsv column requires index or name");
    }
    if (index != null && name != null) {
      throw new IllegalArgumentException("tsv column requires index or name, not both");
    }
  }
}
