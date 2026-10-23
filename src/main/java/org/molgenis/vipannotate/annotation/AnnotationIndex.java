package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.IndexRange;

public interface AnnotationIndex<T extends Feature> {
  /** returns whether this index is empty */
  boolean isEmpty();

  // FIXME update docs
  /** {@return annotation data index range or <code>null</code> if no index exists for variant} */
  boolean findIndexesInto(T feature, int encodedPos, IndexRange indexRange);

  void reset();
}
