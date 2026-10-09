package org.molgenis.vipannotate.annotation;

import java.util.List;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

public interface AnnotationDataset<T extends Annotation> {
  /**
   * find annotations for indexes in the given index range and update existing annotations in the
   * provided {@link List}.
   *
   * <p>this method allows reusing annotations list and its annotations to reduce allocations and
   * garbage collect pressure.
   */
  default void findByIndexesInto(
      IndexRange indexRange, List<T> annotations, Supplier<T> annotationSupplier) {
    for (int i = indexRange.getStart(), end = indexRange.getEnd(); i <= end; ++i) {
      T annotation = findByIndexInto(i, annotationSupplier);
      if (annotation != null) {
        annotations.add(annotation);
      }
    }
  }

  /**
   * finds annotation at the given index and read into a supplied annotation.
   *
   * <p>the supplier is invoked only if an annotation is found
   *
   * @return the populated annotation if found, or {@code null} otherwise
   */
  @Nullable T findByIndexInto(int index, Supplier<T> annotationSupplier);
}
