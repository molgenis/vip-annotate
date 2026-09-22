package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;

public record EnumFieldAnalysis(EnumAnnotationSpec annotationSpec, EnumAnnotationStats stats)
    implements FieldAnalysis {}
