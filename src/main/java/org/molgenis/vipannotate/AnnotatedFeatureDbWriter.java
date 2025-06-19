package org.molgenis.vipannotate;

import java.util.Iterator;
import lombok.NonNull;
import org.molgenis.vipannotate.annotation.AnnotatedFeature;
import org.molgenis.vipannotate.annotation.Annotation;
import org.molgenis.vipannotate.annotation.Feature;

/**
 * Writes annotated features to a database
 *
 * @param <T> type of genomic feature
 * @param <U> type of genomic feature annotation
 * @param <V> annotated genomic feature typed by T and U
 */
public interface AnnotatedFeatureDbWriter<
    T extends Feature, U extends Annotation, V extends AnnotatedFeature<T, U>> {
  void write(@NonNull Iterator<V> annotatedFeatureIt);
}
