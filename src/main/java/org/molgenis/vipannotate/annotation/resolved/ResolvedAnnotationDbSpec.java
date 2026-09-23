package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record ResolvedAnnotationDbSpec(
    // TODO use SemVer class with regex, see https://semver.org
    @JsonProperty(value = "version", required = true) String specVersion,
    // TODO use [a-z0-9._-] and length ≤ 64
    @JsonProperty(value = "id", required = true) String specId,
    @JsonProperty(value = "description") @Nullable String specDescription,
    @JsonProperty(value = "schema", required = true) ResolvedAnnotationSchema annotationSchema) {}
