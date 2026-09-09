package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class IndexedAnnotationEncoder<T extends Annotation> {
  private final AnnotationEncoder<T> annotationEncoder;

  public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
    annotationEncoder.initialize(memoryBuffer);
    //        short encodedNullScore = doubleCodec.encodeDoubleAsShort(null, doubleInterval);
    //        // TODO use .fill(..)
    //        for (int i = indexRange.start(), indexEnd = indexRange.end(); i <= indexEnd; i++)
    // {
    //          memoryBuffer.setShortAtIndex(i, encodedNullScore);
    //        }
    System.err.println("FIXME implement clear(IndexRange indexRange, MemoryBuffer memoryBuffer)");
  }

  public int getAnnotationSizeInBytes() {
    return Short.BYTES;
  }

  public void encodeInto(IndexedAnnotation<T> indexedAnnotation, MemoryBuffer memoryBuffer) {
    annotationEncoder.encodeInto(
        indexedAnnotation.getFeatureAnnotation(), memoryBuffer, indexedAnnotation.getIndex());
  }
}
