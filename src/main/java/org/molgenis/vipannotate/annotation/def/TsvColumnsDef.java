package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record TsvColumnsDef(
    @JsonProperty(value = "contig", required = true) TsvColumnDef contig,
    @JsonProperty(value = "start", required = true) TsvColumnDef start,
    @JsonProperty(value = "end") TsvColumnDef end,
    @JsonProperty(value = "ref") TsvColumnDef ref,
    @JsonProperty(value = "alt") TsvColumnDef alt,
    @JsonProperty(value = "annotations", required = true) Map<String, TsvColumnDef> annotations) {}
