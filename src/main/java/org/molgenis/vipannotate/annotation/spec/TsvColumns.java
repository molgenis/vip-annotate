package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record TsvColumns(
    @JsonProperty(value = "contig", required = true) TsvColumn contig,
    @JsonProperty(value = "start", required = true) TsvColumn start,
    @JsonProperty(value = "end") TsvColumn end,
    @JsonProperty(value = "ref") TsvColumn ref,
    @JsonProperty(value = "alt") TsvColumn alt,
    @JsonProperty(value = "annotations", required = true) Map<String, TsvColumn> annotations) {}
