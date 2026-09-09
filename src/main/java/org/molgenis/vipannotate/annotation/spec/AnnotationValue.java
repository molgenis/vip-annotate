package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record AnnotationValue(
    @JsonProperty(value = "storage_type", required = true) StorageType storageType,
    @JsonProperty(value = "logical_type", required = true) LogicalType logicalType,
    @JsonProperty(value = "encoding") Encoding encoding) {}
