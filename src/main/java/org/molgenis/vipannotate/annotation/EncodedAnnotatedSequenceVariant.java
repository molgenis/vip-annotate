package org.molgenis.vipannotate.annotation;

import lombok.NonNull;

/**
 * Encoded sequence variant
 *
 * @param encodedSequentVariant encoded sequence variant
 * @param annotatedSequenceVariant annotated sequence variant
 * @param <T> type of encoded sequence variant
 * @param <U> type of sequence variant annotation
 */
public record EncodedAnnotatedSequenceVariant<T, U extends Annotation>(
    @NonNull T encodedSequentVariant,
    @NonNull AnnotatedSequenceVariant<U> annotatedSequenceVariant) {}
