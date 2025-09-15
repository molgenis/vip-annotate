package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class IndexedDoubleValueAnnotationToShortEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final DoubleInterval doubleInterval;

  @Override
  public int getAnnotationSizeInBytes() {
    return Short.BYTES;
  }

  @Override
  public void encode(
      IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    int memoryBufferIndex = indexedAnnotation.getIndex() * getAnnotationSizeInBytes();
    short encodedScore =
        doubleCodec.encodeDoubleAsShort(score, doubleInterval.min(), doubleInterval.max());
    memoryBuffer.putInt16(memoryBufferIndex, encodedScore);
  }

  @Override
  public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
    short encodedNullScore =
        doubleCodec.encodeDoubleAsShort(null, doubleInterval.min(), doubleInterval.max());
    for (int i = indexRange.start(), indexEnd = indexRange.end(); i < indexEnd; i++) {
      int memoryBufferIndex = i * getAnnotationSizeInBytes();
      memoryBuffer.putInt16(memoryBufferIndex, encodedNullScore);
    }
  }
}
