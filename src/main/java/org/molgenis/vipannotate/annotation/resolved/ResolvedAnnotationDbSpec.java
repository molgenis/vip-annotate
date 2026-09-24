package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public record ResolvedAnnotationDbSpec(
    // TODO use SemVer class with regex, see https://semver.org
    String specVersion,
    // TODO use [a-z0-9._-] and length ≤ 64
    String specId,
    @Nullable String specDescription,
    ResolvedAnnotationSchema annotationSchema) {}
