package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.*;

import lombok.NonNull;

public record IndexedIntervalAnnotation<T extends IntervalAnnotation<U>, U>(
    int index, @NonNull T annotation) {
  public IndexedIntervalAnnotation {
    validateNonNegative(index);
  }
}
