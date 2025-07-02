package org.molgenis.vipannotate.annotation;

/**
 * Sequence variant encoded as an int
 *
 * @param encodedSequentVariant encoded sequence variant
 * @param annotatedSequenceVariant annotated sequence variant
 * @param <U> type of sequence variant annotation
 */
public record IntEncodedAnnotatedSequenceVariant<U extends Annotation>(
    int encodedSequentVariant, AnnotatedSequenceVariant<U> annotatedSequenceVariant) {}
