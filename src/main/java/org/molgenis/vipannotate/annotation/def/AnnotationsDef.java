package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.*;
import java.util.Map;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record AnnotationsDef(
    @JsonProperty(value = "annotation_type", required = true) AnnotationType annotationType,
    @JsonProperty(value = "annotations", required = true)
        Map<String, AnnotationDef> annotationDefMap) {}
