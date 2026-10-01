package org.molgenis.vipannotate.annotation;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.RankBitmap;

@RequiredArgsConstructor
public class NullableAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private final RankBitmap rankBitmap;
  private final AnnotationDataset<T> annotationDataset;

  @Override
  public @Nullable T findByIndexInto(int index, Supplier<T> annotationSupplier) {
    if (!rankBitmap.isSet(index)) {
      T annotation = annotationSupplier.get();
      annotation.reset();
      return annotation;
    }

    int nonNullIndex = rankBitmap.rank1(index);
    return annotationDataset.findByIndexInto(nonNullIndex, annotationSupplier);
  }
}
