package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public sealed interface ResolvedAnnotationSpec
    permits ResolvedEnumAnnotationSpec,
        ResolvedEnumSetAnnotationSpec,
        ResolvedFloatAnnotationSpec,
        ResolvedIntAnnotationSpec {
  @Nullable String description();
}
