package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public record ResolvedEnumAnnotationSpec(
    @Nullable String description, String[] values, boolean nullable)
    implements ResolvedAnnotationSpec {}
