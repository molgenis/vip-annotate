package org.molgenis.vipannotate.annotation;

import lombok.*;

/**
 * annotated genomic position
 *
 * @param <T> annotation data type
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnnotatedPosition<T extends Annotation> extends AnnotatedInterval<Position, T> {
  public AnnotatedPosition(Position position, T annotation) {
    super(position, annotation);
  }
}
