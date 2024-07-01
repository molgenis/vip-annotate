package org.molgenis.vipannotate.annotation;

import lombok.*;

/**
 * annotated genomic sequence variant
 *
 * @param <T> annotation data type
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnnotatedSequenceVariant<T extends Annotation>
    extends AnnotatedInterval<SequenceVariant, T> {
  public AnnotatedSequenceVariant(SequenceVariant sequenceVariant, T annotation) {
    super(sequenceVariant, annotation);
  }
}
