package org.molgenis.vipannotate.annotation;

public sealed interface AnnotationStats
    permits EnumAnnotationStats, EnumSetAnnotationStats, FloatAnnotationStats, IntAnnotationStats {}
