package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = EnumAnnotationDef.class, name = "enum"),
  @JsonSubTypes.Type(value = EnumSetAnnotationDef.class, name = "enum_set"),
  @JsonSubTypes.Type(value = FloatAnnotationDef.class, name = "floating_point"),
  @JsonSubTypes.Type(value = IntAnnotationDef.class, name = "integer")
})
public sealed interface AnnotationDef
    permits EnumAnnotationDef, EnumSetAnnotationDef, FloatAnnotationDef, IntAnnotationDef {
  // TODO use [a-zA-Z0-9_-<space>]
  @Nullable String description();
}
