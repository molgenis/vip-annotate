package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record EnumAnnotationSpec(
    @JsonProperty(value = "values", required = true) String[] values,
    @JsonProperty(value = "nullable") boolean nullable)
    implements AnnotationSpec {}
