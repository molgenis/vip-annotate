package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnnotationDataset(
    @JsonProperty(value = "id", required = true) String id,
    @JsonProperty(value = "annotation_value", required = true) AnnotationValue annotationValue) {}
