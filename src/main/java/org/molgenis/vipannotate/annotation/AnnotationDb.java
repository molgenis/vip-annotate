package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface AnnotationDb<T extends Feature, U extends Annotation>
    extends AutoCloseableNoThrow {
  /** find and return annotations for the given feature. */
  default List<U> findAnnotations(T feature) {
    ArrayList<U> annotations = new ArrayList<>();
    findAnnotations(feature, annotations);
    return annotations;
  }

  /**
   * find annotations for the given feature and add to the provided {@link List}
   *
   * <p>this method allows reusing annotations list to reduce allocations and garbage collect
   * pressure.
   */
  void findAnnotations(T feature, List<U> annotations);
}
