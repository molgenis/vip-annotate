package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.nio.file.Path;
import java.util.Map;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeName("bed")
public record BedInputFormat(
    @JsonProperty(value = "file", required = true) Path file,
    @JsonProperty(value = "annotations", required = true) Map<String, BedField> annotations)
    implements InputFormat {}
