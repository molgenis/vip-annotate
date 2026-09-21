package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record OffsetNullableIntEncoding(@JsonProperty(value = "offset", required = true) int offset)
    implements IntEncoding {}
