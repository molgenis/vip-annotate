package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

public interface AnnotationIndex<T extends Feature> {
  /**
   * @return annotation data index range or <code>null</code> if no index exists for variant
   */
  @Nullable IndexRange findIndexes(T feature);
}
