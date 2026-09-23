package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record EnumSetAnnotationSpec(
    @JsonProperty(value = "description") @Nullable String description,
    @JsonProperty(value = "values", required = true) String[] values)
    implements AnnotationSpec {}
