package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record EnumValue(
    @JsonProperty(value = "value", required = true) String value,
    @JsonProperty(value = "description") @Nullable String description) {}
