package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnnotationValue(
    @JsonProperty(value = "storage_type", required = true) StorageType storageType,
    @JsonProperty(value = "logical_type", required = true) LogicalType logicalType,
    @JsonProperty(value = "encoding") Encoding encoding) {}
