package org.molgenis.vipannotate.annotation;

import java.util.Iterator;

/**
 * Writes annotated features to a database
 *
 * @param <T> type of genomic feature
 * @param <U> type of genomic feature annotation
 * @param <V> annotated genomic feature typed by T and U
 */
public interface AnnotatedFeatureDbWriter<
    T extends Feature, U extends Annotation, V extends AnnotatedFeature<T, U>> {
  void write(Iterator<V> annotatedFeatureIt);
}
