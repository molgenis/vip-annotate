package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;

public record EnumSetAnnotationAnalysis(
    EnumSetAnnotationSpec annotationSpec, EnumSetAnnotationStats stats)
    implements AnnotationAnalysis {}
