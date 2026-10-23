package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

public record IntAnnotationDef(@JsonProperty(value = "description") @Nullable String description)
    implements AnnotationDef {}
