package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

public interface AnnotationDataset<T extends @Nullable Annotation> {
  /** {@return annotation dataset collection for indexes in the given index range} */
  default List<T> findByIndexes(IndexRange indexRange) {
    List<T> annotations = new ArrayList<>(indexRange.end() - indexRange.start() + 1);
    findByIndexes(indexRange, annotations);
    return annotations;
  }

  /**
   * find annotations for indexes in the given index range and add to the provided {@link List}.
   *
   * <p>this method allows reusing annotations list to reduce allocations and garbage collect
   * pressure.
   */
  default void findByIndexes(IndexRange indexRange, List<T> annotations) {
    for (int i = indexRange.start(), end = indexRange.end(); i <= end; ++i) {
      T annotation = findByIndex(i);
      annotations.add(annotation);
    }
  }

  /** {@return annotation dataset or <code>null</code> for the given index} */
  @Nullable T findByIndex(int index);
}
