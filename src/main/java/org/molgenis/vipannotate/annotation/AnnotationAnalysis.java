package org.molgenis.vipannotate.annotation;

public sealed interface AnnotationAnalysis
    permits EnumAnnotationAnalysis,
        EnumSetAnnotationAnalysis,
        FloatAnnotationAnalysis,
        IntAnnotationAnalysis {}
