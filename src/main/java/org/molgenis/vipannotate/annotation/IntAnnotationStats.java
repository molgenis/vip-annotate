package org.molgenis.vipannotate.annotation;

public record IntAnnotationStats(long count, long nullCount, long min, long max)
    implements AnnotationStats {}
