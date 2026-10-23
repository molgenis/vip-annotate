package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

public record EnumSetAnnotationDef(
    @JsonProperty(value = "description") @Nullable String description,
    @JsonProperty(value = "values", required = true) EnumValue[] values)
    implements AnnotationDef {}
