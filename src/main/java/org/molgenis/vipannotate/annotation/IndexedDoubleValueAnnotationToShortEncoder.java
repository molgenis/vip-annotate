package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
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
  public void encodeInto(
      IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    short encodedScore = doubleCodec.encodeDoubleAsShort(score, doubleInterval);
    memoryBuffer.setShortAtIndex(indexedAnnotation.getIndex(), encodedScore);
  }

  @Override
  public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
    short encodedNullScore = doubleCodec.encodeDoubleAsShort(null, doubleInterval);
    // TODO use .fill(..)
    for (int i = indexRange.start(), indexEnd = indexRange.end(); i <= indexEnd; i++) {
      memoryBuffer.setShortAtIndex(i, encodedNullScore);
    }
  }
}
