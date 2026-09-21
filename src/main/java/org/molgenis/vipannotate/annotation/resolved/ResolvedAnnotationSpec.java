package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ResolvedEnumAnnotationSpec.class, name = "enum"),
  @JsonSubTypes.Type(value = ResolvedEnumSetAnnotationSpec.class, name = "enum_set"),
  @JsonSubTypes.Type(value = ResolvedFloatAnnotationSpec.class, name = "floating_point"),
  @JsonSubTypes.Type(value = ResolvedIntAnnotationSpec.class, name = "integer")
})
public sealed interface ResolvedAnnotationSpec
    permits ResolvedEnumAnnotationSpec,
        ResolvedEnumSetAnnotationSpec,
        ResolvedFloatAnnotationSpec,
        ResolvedIntAnnotationSpec {}
