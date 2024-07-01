package org.molgenis.vipannotate.annotation;

/**
 * Sequence variant with generic encoding
 *
 * @param encodedSequentVariant encoded sequence variant
 * @param annotatedSequenceVariant annotated sequence variant
 * @param <T> type of encoded sequence variant
 * @param <U> type of sequence variant annotation
 */
public record EncodedAnnotatedSequenceVariant<T, U extends Annotation>(
    T encodedSequentVariant, AnnotatedSequenceVariant<U> annotatedSequenceVariant) {}
