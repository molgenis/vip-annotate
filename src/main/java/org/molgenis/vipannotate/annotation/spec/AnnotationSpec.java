package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnnotationSpec(
    // TODO use [a-z0-9._-] and length ≤ 64
    @JsonProperty(value = "id", required = true) String specId,
    // TODO use SemVer class with regex, see https://semver.org
    @JsonProperty(value = "version", required = true) String specVersion,
    @JsonProperty(value = "input", required = true) InputFormat inputFormat,
    @JsonProperty(value = "schema", required = true) AnnotationSchema annotationSchema,
    @JsonProperty(value = "output", required = true) OutputFormat outputFormat) {}
