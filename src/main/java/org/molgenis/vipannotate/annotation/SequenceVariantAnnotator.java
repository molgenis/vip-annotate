package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SequenceVariantAnnotator<T extends Annotation> implements AutoCloseableNoThrow {
  private final Predicate<SequenceVariant> canAnnotate;
  private final AnnotationDb<SequenceVariant, T> annotationDb;
  private final Pool<T> annotationPool;
  private final AnnotationSelector<T> annotationSelector;

  // perf: reduce allocations and garbage collect pressure
  @Nullable private List<T> reusableAltAnnotations;

  public void annotate(SequenceVariant sequenceVariant, Consumer<T> consumer) {
    if (canAnnotate.test(sequenceVariant)) {
      List<T> altAnnotations = createAnnotationList();
      try {
        annotationDb.findAnnotations(sequenceVariant, altAnnotations);
        consumer.accept(annotationSelector.select(altAnnotations));
      } finally {
        annotationPool.releaseAll(altAnnotations);
      }

    } else {
      consumer.accept(null);
    }
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
