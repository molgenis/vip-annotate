package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

public class SequenceVariantAnnotationIndexDispatcherWriter<T extends SequenceVariant>
    implements MemoryBufferWriter<SequenceVariantAnnotationIndexDispatcher<T>> {
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
  public MemoryBuffer writeTo(SequenceVariantAnnotationIndexDispatcher<T> object) {
    MemoryBuffer memBuffer = memBufferFactory.newMemoryBuffer();
    writeInto(object, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(
      SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher, MemoryBuffer memoryBuffer) {
    // TODO perf: write control byte that indicates which types are serialized
    for (Type type : Type.values()) {
      MemoryBufferWriter<AnnotationIndex<T>> writer = writerMap.get(type);
      if (writer != null) {
        writer.writeInto(indexDispatcher.getAnnotationIndex(type), memoryBuffer);
      }
    }
  }
}
