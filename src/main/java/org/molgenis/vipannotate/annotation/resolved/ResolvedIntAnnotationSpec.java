package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record ResolvedIntAnnotationSpec(
    @JsonProperty(value = "storage_type", required = true) IntType storageType,
    @JsonProperty(value = "encoding", required = true) IntEncoding intEncoding)
    implements ResolvedAnnotationSpec {}
