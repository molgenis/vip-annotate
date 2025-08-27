package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

public interface AnnotationDataset<T extends @Nullable Annotation> {
  /**
   * @return annotation dataset collection for indexes in the given index range
   */
  default List<T> findByIndexes(IndexRange indexRange) {
    int start = indexRange.start();
    int end = indexRange.end();
    int nrAnnotations = end - start + 1;

    if (nrAnnotations == 1) {
      T annotation = findByIndex(start);
      return Collections.singletonList(annotation);
    } else {
      List<T> annotations = new ArrayList<>(nrAnnotations);
      for (int i = start; i <= end; ++i) {
        T annotation = findByIndex(i);
        annotations.add(annotation);
      }
      return annotations;
    }
  }

  /**
   * @return annotation dataset or <code>null</code> for the given index
   */
  T findByIndex(int index);
}
