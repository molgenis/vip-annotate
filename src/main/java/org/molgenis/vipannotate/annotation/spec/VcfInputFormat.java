package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonTypeName;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeName("vcf")
public record VcfInputFormat() implements InputFormat {}
