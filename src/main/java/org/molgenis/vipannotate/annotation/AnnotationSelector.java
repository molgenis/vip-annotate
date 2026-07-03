package org.molgenis.vipannotate.annotation;

import java.util.List;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
interface AnnotationSelector<T extends Annotation> {
  @Nullable T select(List<T> annotationCandidates);
}
