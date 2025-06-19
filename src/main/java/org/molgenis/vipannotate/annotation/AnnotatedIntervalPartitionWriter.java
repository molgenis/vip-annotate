package org.molgenis.vipannotate.annotation;

/**
 * Writes an annotated genomic interval partition
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic interval annotation
 * @param <V> annotated genomic interval typed by T and U
 */
public interface AnnotatedIntervalPartitionWriter<
    T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>> {
  void write(Partition<T, U, V> partition);
}
