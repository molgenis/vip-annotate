package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

public class SequenceVariantAnnotationIndexDispatcherReader<T extends SequenceVariant>
    implements MemoryDecodable<SequenceVariantAnnotationIndexDispatcher<T>> {
  private final EnumMap<EncodedSequenceVariant.Type, MemoryDecodable<AnnotationIndex<T>>>
      serializerMap;

  public SequenceVariantAnnotationIndexDispatcherReader() {
    serializerMap = new EnumMap<>(EncodedSequenceVariant.Type.class);
  }

  public void register(
      EncodedSequenceVariant.Type type, MemoryDecodable<AnnotationIndex<T>> serializer) {
    serializerMap.put(type, serializer);
  }

  @Override
  public SequenceVariantAnnotationIndexDispatcher<T> readFrom(MemoryDecoder memDecoder) {
    memDecoder.unwrap().flip(); // FIXME should not happen here

    EnumMap<EncodedSequenceVariant.Type, AnnotationIndex<T>> indexMap =
        new EnumMap<>(EncodedSequenceVariant.Type.class);
    for (EncodedSequenceVariant.Type type : EncodedSequenceVariant.Type.values()) {
      MemoryDecodable<AnnotationIndex<T>> reader = serializerMap.get(type);
      if (reader != null) {
        indexMap.put(type, reader.readFrom(memDecoder));
      }
    }
    return new SequenceVariantAnnotationIndexDispatcher<>(indexMap);
  }

  @Override
  public void readInto(
      MemoryDecoder memDecoder, SequenceVariantAnnotationIndexDispatcher<T> indexDispatcher) {
    memDecoder.unwrap().flip(); // FIXME should not happen here

    for (EncodedSequenceVariant.Type type : EncodedSequenceVariant.Type.values()) {
      MemoryDecodable<AnnotationIndex<T>> reader = serializerMap.get(type);
      if (reader != null) {
        reader.readInto(memDecoder, indexDispatcher.getAnnotationIndex(type));
      }
    }
  }
}
