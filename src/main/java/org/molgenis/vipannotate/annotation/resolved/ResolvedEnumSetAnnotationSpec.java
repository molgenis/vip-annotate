package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public record ResolvedEnumSetAnnotationSpec(@Nullable String description, String[] values)
    implements ResolvedAnnotationSpec {}
