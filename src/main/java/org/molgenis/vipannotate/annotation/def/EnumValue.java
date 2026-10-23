package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

public record EnumValue(
    @JsonProperty(value = "value", required = true) String value,
    @JsonProperty(value = "description") @Nullable String description) {}
