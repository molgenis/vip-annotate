package org.molgenis.vipannotate.db.v2;

import org.molgenis.vipannotate.db.exact.Variant;

public interface AnnotationIndex {
  /**
   * @return non-negative annotation data index or <code>-1</code> if no index exists for variant
   */
  int findIndex(Variant variant);
}
