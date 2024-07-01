package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;

public class SequenceVariantAnnotationIndexDispatcherFactory<T extends SequenceVariant> {
  private SequenceVariantAnnotationIndexDispatcherFactory() {}

  /** create a new index dispatcher with empty indexes */
  public static <T extends SequenceVariant> SequenceVariantAnnotationIndexDispatcher<T> create() {
    EnumMap<Type, AnnotationIndex<T>> indexMap = new EnumMap<>(Type.class);
    indexMap.put(Type.SMALL, SequenceVariantAnnotationIndexSmallFactory.create());
    indexMap.put(Type.BIG, SequenceVariantAnnotationIndexBigFactory.create());
    return new SequenceVariantAnnotationIndexDispatcher<>(indexMap);
  }
}
