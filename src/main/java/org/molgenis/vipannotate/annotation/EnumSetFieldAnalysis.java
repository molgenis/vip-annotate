package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;

public record EnumSetFieldAnalysis(
    EnumSetAnnotationSpec annotationSpec, EnumSetAnnotationStats stats) implements FieldAnalysis {}
