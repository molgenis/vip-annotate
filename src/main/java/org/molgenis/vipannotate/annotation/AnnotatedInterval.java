package org.molgenis.vipannotate.annotation;

import lombok.*;
import org.jspecify.annotations.Nullable;

/**
 * Annotated genomic interval
 *
 * @param <T> type of genomic interval
 * @param <U> type of annotation
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class AnnotatedInterval<T extends Interval, U extends @Nullable Annotation>
    implements AnnotatedFeature<T, U> {
  /** genome interval */
  private final T feature;

  /** annotation data */
  private final U annotation;
}
