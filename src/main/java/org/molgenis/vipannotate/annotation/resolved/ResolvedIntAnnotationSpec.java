package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public record ResolvedIntAnnotationSpec(
    @Nullable String description, IntType storageType, IntEncoding intEncoding)
    implements ResolvedAnnotationSpec {}
