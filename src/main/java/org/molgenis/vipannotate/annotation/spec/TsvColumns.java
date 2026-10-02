package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record TsvColumns(
    @JsonProperty(value = "contig", required = true) int contig,
    @JsonProperty(value = "start", required = true) int start,
    @JsonProperty(value = "end") Integer end,
    @JsonProperty(value = "ref") Integer ref,
    @JsonProperty(value = "alt") Integer alt,
    @JsonProperty(value = "annotations", required = true) Map<String, Integer> annotations) {}
