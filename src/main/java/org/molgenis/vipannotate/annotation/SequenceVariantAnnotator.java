package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SequenceVariantAnnotator<T extends Annotation> implements AutoCloseableNoThrow {
  private final Predicate<SequenceVariant> canAnnotate;
  private final AnnotationDb<SequenceVariant, T> annotationDb;
  private final AnnotationSelector<T> annotationSelector;

  // perf: reduce allocations and garbage collect pressure
  @Nullable private List<T> reusableAltAnnotations;

  public @Nullable T annotate(SequenceVariant sequenceVariant) {
    T altAnnotation;
    if (canAnnotate.test(sequenceVariant)) {
      List<T> altAnnotations = createAnnotationList();
      annotationDb.findAnnotations(sequenceVariant, altAnnotations);

      altAnnotation = annotationSelector.select(altAnnotations);

    } else {
      altAnnotation = null;
    }
    return altAnnotation;
  }

  private List<T> createAnnotationList() {
    if (reusableAltAnnotations == null) {
      reusableAltAnnotations = new ArrayList<>(1);
    } else {
      reusableAltAnnotations.clear();
    }
    return reusableAltAnnotations;
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDb);
  }
}
