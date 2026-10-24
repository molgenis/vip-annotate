package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryEncodable;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public class SequenceVariantAnnotationIndexDispatcherWriter<T extends SequenceVariant>
    implements MemoryEncodable<SequenceVariantAnnotationIndexDispatcher<T>> {
  private final MemoryBufferFactory memBufferFactory;
  private final EnumMap<EncodedSequenceVariant.Type, MemoryEncodable<AnnotationIndex<T>>> writerMap;

  public SequenceVariantAnnotationIndexDispatcherWriter(MemoryBufferFactory memBufferFactory) {
    this.memBufferFactory = memBufferFactory;
    this.writerMap = new EnumMap<>(EncodedSequenceVariant.Type.class);
  }

  public void register(
      EncodedSequenceVariant.Type type, MemoryEncodable<AnnotationIndex<T>> writer) {
    writerMap.put(type, writer);
  }

  @Override
  public MemoryEncoder writeTo(SequenceVariantAnnotationIndexDispatcher<T> object) {
    MemoryEncoder memEncoder = MemoryEncoder.wrap(memBufferFactory.newMemoryBuffer());
    writeInto(object, memEncoder);
    return memEncoder;
  }

  @Override
  public void writeInto(
      SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher, MemoryEncoder memEncoder) {
    // TODO perf: write control byte that indicates which types are serialized
    for (EncodedSequenceVariant.Type type : EncodedSequenceVariant.Type.values()) {
      MemoryEncodable<AnnotationIndex<T>> writer = writerMap.get(type);
      if (writer != null) {
        writer.writeInto(indexDispatcher.getAnnotationIndex(type), memEncoder);
      }
    }
  }
}
