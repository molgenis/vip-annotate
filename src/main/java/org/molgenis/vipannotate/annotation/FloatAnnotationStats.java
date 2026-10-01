package org.molgenis.vipannotate.annotation;

public record FloatAnnotationStats(long count, long nullCount, double min, double max)
    implements AnnotationStats {}
