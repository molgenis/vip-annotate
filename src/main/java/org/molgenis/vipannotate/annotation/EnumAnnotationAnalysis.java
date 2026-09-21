package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;

public record EnumAnnotationAnalysis(EnumAnnotationSpec annotationSpec, EnumAnnotationStats stats)
    implements AnnotationAnalysis {}
