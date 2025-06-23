package org.molgenis.vipannotate.annotation;

import java.util.Collection;
import java.util.List;
import lombok.NonNull;

public record AnnotationCollection<T extends Annotation>(@NonNull Collection<T> annotations)
    implements Annotation {
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static final AnnotationCollection EMPTY = new AnnotationCollection(List.of());

  @SuppressWarnings("unchecked")
  public static <T extends Annotation> AnnotationCollection<T> empty() {
    return (AnnotationCollection<T>) EMPTY;
  }
}
