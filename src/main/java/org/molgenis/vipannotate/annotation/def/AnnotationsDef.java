package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.*;
import java.util.Map;

public record AnnotationsDef(
    @JsonProperty(value = "annotation_type", required = true) AnnotationType annotationType,
    @JsonProperty(value = "annotations", required = true)
        Map<String, AnnotationDef> annotationDefMap) {}
