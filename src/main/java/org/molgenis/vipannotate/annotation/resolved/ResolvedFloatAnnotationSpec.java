package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public record ResolvedFloatAnnotationSpec(
    @Nullable String description, ScalarType storageType, FloatEncoding floatEncoding)
    implements ResolvedAnnotationSpec {}
