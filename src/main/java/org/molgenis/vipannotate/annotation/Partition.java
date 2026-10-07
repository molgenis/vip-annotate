package org.molgenis.vipannotate.annotation;

import java.util.List;

/**
 * Partition of annotated genomic intervals
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic interval annotation
 * @param <V> annotated genomic interval typed by T and U
 */
public record Partition<
    T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>(
    PartitionKey key, List<V> annotatedIntervals) {}
