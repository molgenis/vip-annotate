package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record TsvColumn(
    @JsonProperty(value = "index") @Nullable Integer index,
    @JsonProperty(value = "name") @Nullable String name) {
  public TsvColumn {
    if (index == null && name == null) {
      throw new IllegalArgumentException("tsv column requires index or name");
    }
    if (index != null && name != null) {
      throw new IllegalArgumentException("tsv column requires index or name, not both");
    }
  }
}
