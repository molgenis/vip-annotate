package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

public class SequenceVariantAnnotationIndexDispatcherSerializer<T extends SequenceVariant>
    implements BinarySerializer<AnnotationIndex<T>> {
  private final EnumMap<Type, BinarySerializer<AnnotationIndex<T>>> serializerMap;

  public SequenceVariantAnnotationIndexDispatcherSerializer() {
    serializerMap = new EnumMap<>(Type.class);
  }

  public void register(Type type, BinarySerializer<AnnotationIndex<T>> serializer) {
    serializerMap.put(type, serializer);
  }

  @Override
  public void writeTo(MemoryBuffer memoryBuffer, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher = getTyped(index);

    // TODO perf: write control byte that indicates which types are serialized
    for (Type type : Type.values()) {
      getSerializer(type).writeTo(memoryBuffer, indexDispatcher.getAnnotationIndex(type));
    }
  }

  @Override
  public SequenceVariantAnnotationIndexDispatcher<T> readFrom(MemoryBuffer memoryBuffer) {
    EnumMap<Type, AnnotationIndex<T>> indexMap = new EnumMap<>(Type.class);
    for (Type type : Type.values()) {
      indexMap.put(type, getSerializer(type).readFrom(memoryBuffer));
    }
    return new SequenceVariantAnnotationIndexDispatcher<>(indexMap);
  }

  @Override
  public void readInto(MemoryBuffer memoryBuffer, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher = getTyped(index);

    for (Type type : Type.values()) {
      getSerializer(type).readInto(memoryBuffer, indexDispatcher.getAnnotationIndex(type));
    }
  }

  @Override
  public SequenceVariantAnnotationIndexDispatcher<T> readEmpty() {
    return SequenceVariantAnnotationIndexDispatcherFactory.create();
  }

  private BinarySerializer<AnnotationIndex<T>> getSerializer(Type type) {
    BinarySerializer<AnnotationIndex<T>> serializer = serializerMap.get(type);
    if (serializer == null) {
      throw new EnumConstantNotPresentException(Type.class, type.toString());
    }
    return serializer;
  }

  private SequenceVariantAnnotationIndexDispatcher<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher)) {
      throw new IllegalArgumentException(
          "index must be of type SequenceVariantAnnotationIndexDispatcher");
    }
    return indexDispatcher;
  }
}
