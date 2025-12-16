package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.util.IndexRange;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexDispatcher<T extends SequenceVariant>
    implements AnnotationIndex<T> {
  private final EnumMap<Type, AnnotationIndex<T>> indexMap;

  public SequenceVariantAnnotationIndexDispatcher() {
    indexMap = new EnumMap<>(Type.class);
  }

  public void register(Type type, AnnotationIndex<T> index) {
    indexMap.put(type, index);
  }

  @Override
  public boolean isEmpty() {
    for (AnnotationIndex<T> index : indexMap.values()) {
      if (!index.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public @Nullable IndexRange findIndexes(T feature) {
    if (isEmpty()) {
      return null;
    }

    Type type = SequenceVariantEncoderUtils.determineType(feature);
    AnnotationIndex<T> annotationIndex = indexMap.get(type);
    if (annotationIndex == null) {
      return null;
    }
    return annotationIndex.findIndexes(feature);
  }

  public AnnotationIndex<T> getAnnotationIndex(Type type) {
    AnnotationIndex<T> index = indexMap.get(type);
    if (index == null) {
      throw new EnumConstantNotPresentException(Type.class, type.toString());
    }
    return index;
  }

  /** clear index */
  @Override
  public void reset() {
    for (AnnotationIndex<T> index : indexMap.values()) {
      index.reset();
    }
  }
}
