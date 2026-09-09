package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.nio.file.Path;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeName("bed")
public record BedInputFormat(Path file, BedField from) implements InputFormat {}
