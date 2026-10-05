package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record AnnotationDbDef(
    // TODO use SemVer class with regex, see https://semver.org
    @JsonProperty(value = "version", required = true) String version,
    // TODO use [a-z0-9._-] and length ≤ 64
    @JsonProperty(value = "id", required = true) String id,
    // TODO use [a-zA-Z0-9_-<space>]
    @JsonProperty(value = "description") @Nullable String description,
    @JsonProperty(value = "input", required = true) InputFormat inputFormat,
    @JsonProperty(value = "definition", required = true) AnnotationsDef annotationsDef) {}
