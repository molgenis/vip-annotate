package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.molgenis.vipannotate.annotation.spec.OutputFormat;

// FIXME do not depend on spec: do not use spec.OutputFormat
// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record ResolvedAnnotationDbSpec(
    @JsonProperty(value = "schema", required = true) ResolvedAnnotationSchema annotationSchema,
    @JsonProperty(value = "output", required = true) OutputFormat outputFormat) {}
