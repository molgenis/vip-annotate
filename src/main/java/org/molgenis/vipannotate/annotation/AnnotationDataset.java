package org.molgenis.vipannotate.annotation;

import java.util.List;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

// FIXME remove nullable?
public interface AnnotationDataset<T extends @Nullable Annotation> {
  /**
   * find annotations for indexes in the given index range and update existing annotations in the
   * provided {@link List}.
   *
   * <p>this method allows reusing annotations list and its annotations to reduce allocations and
   * garbage collect pressure.
   */
  default void findByIndexesInto(
      IndexRange indexRange, List<T> annotations, Supplier<T> annotationSupplier) {
    for (int i = indexRange.start(), end = indexRange.end(); i <= end; ++i) {
      T annotation = annotationSupplier.get();
      if (findByIndexInto(i, annotation)) {
        annotations.add(annotation);
      }
    }
  }

  /**
   * find annotation for given index and read into the given annotation.
   *
   * @return {@code true} if annotation was found, {@code true} false otherwise.
   */
  boolean findByIndexInto(int index, T annotation);
}
