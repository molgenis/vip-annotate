package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.*;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record AnnotationSchema(
    @JsonProperty(value = "annotation_type", required = true) AnnotationType annotationType,
    @JsonProperty(value = "annotations", required = true) AnnotationSpecs annotationSpecs,
    @JsonProperty(value = "annotation_selector", required = true)
        AnnotationSelector annotationSelector) {}
