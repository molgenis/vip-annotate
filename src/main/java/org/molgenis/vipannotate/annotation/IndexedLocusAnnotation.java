package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.*;

import lombok.NonNull;

public record IndexedLocusAnnotation<T extends LocusAnnotation<U>, U>(
    int index, @NonNull T annotation) {
  public IndexedLocusAnnotation {
    validateNonNegative(index);
  }
}
