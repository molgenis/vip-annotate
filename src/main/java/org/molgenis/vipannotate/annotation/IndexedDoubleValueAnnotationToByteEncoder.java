package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class IndexedDoubleValueAnnotationToByteEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final DoubleInterval valueInterval;

  @Override
  public int getAnnotationSizeInBytes() {
    return Byte.BYTES;
  }

  @Override
  public void encode(
      IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    byte encodedScore = doubleCodec.encodeDoubleAsByte(score, valueInterval);
    memoryBuffer.setByteAtIndex(
        (long) indexedAnnotation.getIndex() * getAnnotationSizeInBytes(), encodedScore);
  }

  @Override
  public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
    byte encodedNullScore = doubleCodec.encodeDoubleAsByte(null, valueInterval);
    for (int i = indexRange.start(), indexEnd = indexRange.end(); i < indexEnd; i++) {
      memoryBuffer.setByteAtIndex((long) i * getAnnotationSizeInBytes(), encodedNullScore);
    }
  }
}
