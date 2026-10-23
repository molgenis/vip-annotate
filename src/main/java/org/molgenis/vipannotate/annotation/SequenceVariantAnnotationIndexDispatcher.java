package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.IndexRange;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexDispatcher<T extends SequenceVariant> {
  // perf: reduce memory allocations since indexMap.values() creates new array
  private static final EncodedSequenceVariant.Type[] TYPES = EncodedSequenceVariant.Type.values();
  private final EnumMap<EncodedSequenceVariant.Type, AnnotationIndex<T>> indexMap;

  public SequenceVariantAnnotationIndexDispatcher() {
    indexMap = new EnumMap<>(EncodedSequenceVariant.Type.class);
  }

  public void register(EncodedSequenceVariant.Type type, AnnotationIndex<T> index) {
    indexMap.put(type, index);
  }

  public boolean isEmpty() {
    for (EncodedSequenceVariant.Type type : TYPES) {
      AnnotationIndex<T> index = indexMap.get(type);
      if (index != null && !index.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public boolean findIndexesInto(
      T feature, PositionEncoding positionEncoding, IndexRange indexRange) {
    if (isEmpty()) {
      return false;
    }

    EncodedSequenceVariant.Type type =
        SequenceVariantEncoderUtils.determineType(feature, positionEncoding.bits());
    AnnotationIndex<T> annotationIndex = indexMap.get(type);
    if (annotationIndex == null) {
      return false;
    }
    return annotationIndex.findIndexesInto(feature, positionEncoding.value(), indexRange);
  }

  public AnnotationIndex<T> getAnnotationIndex(EncodedSequenceVariant.Type type) {
    AnnotationIndex<T> index = indexMap.get(type);
    if (index == null) {
      throw new EnumConstantNotPresentException(EncodedSequenceVariant.Type.class, type.toString());
    }
    return index;
  }

  /** clear index */
  public void reset() {
    for (EncodedSequenceVariant.Type type : TYPES) {
      AnnotationIndex<T> index = indexMap.get(type);
      if (index != null) {
        index.reset();
      }
    }
  }
}
