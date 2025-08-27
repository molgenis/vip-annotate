package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

@Deprecated
@RequiredArgsConstructor
public class SequenceVariantGeneContextAnnotationIndex
    implements AnnotationIndex<SequenceVariantGeneContext> {
  private final SequenceVariantGeneContextAnnotationIndexSmall annotationIndexSmall;
  private final SequenceVariantGeneContextAnnotationIndexBig annotationIndexBig;

  public @Nullable IndexRange findIndexes(SequenceVariantGeneContext feature) {
    return null;
  }
}
