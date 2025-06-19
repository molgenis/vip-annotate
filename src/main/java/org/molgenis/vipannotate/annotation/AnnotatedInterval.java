package org.molgenis.vipannotate.annotation;

import lombok.*;

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
public class AnnotatedInterval<T extends Interval, U extends Annotation>
    implements AnnotatedFeature<T, U> {
  /** genome interval */
  @NonNull private final T feature;

  /** annotation data */
  private final U annotation;
}
