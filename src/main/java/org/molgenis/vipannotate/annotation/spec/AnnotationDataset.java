package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record AnnotationDataset(
    @JsonProperty(value = "id", required = true) String id,
    @JsonProperty(value = "annotation_value", required = true) AnnotationValue annotationValue) {}
