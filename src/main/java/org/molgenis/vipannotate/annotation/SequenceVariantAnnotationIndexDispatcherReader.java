package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class SequenceVariantAnnotationIndexDispatcherReader<T extends SequenceVariant>
    implements MemoryBufferReader<AnnotationIndex<T>> {
  private final EnumMap<Type, MemoryBufferReader<AnnotationIndex<T>>> serializerMap;

  public SequenceVariantAnnotationIndexDispatcherReader() {
    serializerMap = new EnumMap<>(Type.class);
  }

  public void register(Type type, MemoryBufferReader<AnnotationIndex<T>> serializer) {
    serializerMap.put(type, serializer);
  }

  @Override
  public SequenceVariantAnnotationIndexDispatcher<T> readFrom(MemoryBuffer memoryBuffer) {
    EnumMap<Type, AnnotationIndex<T>> indexMap = new EnumMap<>(Type.class);
    for (Type type : Type.values()) {
      MemoryBufferReader<AnnotationIndex<T>> reader = serializerMap.get(type);
      if (reader != null) {
        indexMap.put(type, reader.readFrom(memoryBuffer));
      }
    }
    return new SequenceVariantAnnotationIndexDispatcher<>(indexMap);
  }

  @Override
  public void readInto(MemoryBuffer memoryBuffer, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher = getTyped(index);

    for (Type type : Type.values()) {
      MemoryBufferReader<AnnotationIndex<T>> reader = serializerMap.get(type);
      if (reader != null) {
        reader.readInto(memoryBuffer, indexDispatcher.getAnnotationIndex(type));
      }
    }
  }

  // TODO dedup with writer
  private SequenceVariantAnnotationIndexDispatcher<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher)) {
      throw new IllegalArgumentException(
          "index must be of type SequenceVariantAnnotationIndexDispatcher");
    }
    return indexDispatcher;
  }
}
