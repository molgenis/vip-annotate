package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = EnumAnnotationSpec.class, name = "enum"),
  @JsonSubTypes.Type(value = EnumSetAnnotationSpec.class, name = "enum_set"),
  @JsonSubTypes.Type(value = FloatAnnotationSpec.class, name = "floating_point"),
  @JsonSubTypes.Type(value = IntAnnotationSpec.class, name = "integer")
})
public sealed interface AnnotationSpec
    permits EnumAnnotationSpec, EnumSetAnnotationSpec, FloatAnnotationSpec, IntAnnotationSpec {
  // TODO use [a-zA-Z0-9_-<space>]
  @Nullable String description();
}
