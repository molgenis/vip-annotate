package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

public class SequenceVariantAnnotationIndexDispatcherWriter<T extends SequenceVariant>
    implements MemoryBufferWriter<AnnotationIndex<T>> {
  private final MemoryBufferFactory memBufferFactory;
  private final EnumMap<Type, MemoryBufferWriter<AnnotationIndex<T>>> writerMap;

  public SequenceVariantAnnotationIndexDispatcherWriter(MemoryBufferFactory memBufferFactory) {
    this.memBufferFactory = memBufferFactory;
    this.writerMap = new EnumMap<>(Type.class);
  }

  public void register(Type type, MemoryBufferWriter<AnnotationIndex<T>> writer) {
    writerMap.put(type, writer);
  }

  @Override
  public MemoryBuffer writeTo(AnnotationIndex<T> object) {
    MemoryBuffer memBuffer = memBufferFactory.newMemoryBuffer();
    writeInto(object, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(AnnotationIndex<T> index, MemoryBuffer memoryBuffer) {
    SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher = getTyped(index);

    // TODO perf: write control byte that indicates which types are serialized
    for (Type type : Type.values()) {
      MemoryBufferWriter<AnnotationIndex<T>> writer = writerMap.get(type);
      if (writer != null) {
        writer.writeInto(indexDispatcher.getAnnotationIndex(type), memoryBuffer);
      }
    }
  }

  // TODO dedup with reader
  private SequenceVariantAnnotationIndexDispatcher<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher)) {
      throw new IllegalArgumentException(
          "index must be of type SequenceVariantAnnotationIndexDispatcher");
    }
    return indexDispatcher;
  }
}
