package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record ResolvedFloatAnnotationSpec(
    @JsonProperty(value = "description") @Nullable String description,
    @JsonProperty(value = "storage_type", required = true) ScalarType storageType,
    @JsonProperty(value = "encoding", required = true) FloatEncoding floatEncoding)
    implements ResolvedAnnotationSpec {}
