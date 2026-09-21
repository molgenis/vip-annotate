package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class PerElementAnnotationDatasetEncoder<T extends Annotation>
    implements AnnotationDatasetEncoder<T> {
  private final AnnotationEncoder<T> annotationEncoder;

  @Override
  public void encode(SizedIterator<T> annotationIt, int maxAnnotations, MemoryBuffer memBuffer) {
    // FIXME deal with -1 index
    annotationIt.forEachRemaining(value -> annotationEncoder.encodeInto(value, memBuffer, -1));
  }

  @Override
  public long getEncodedSizeInBytes(int annotationCount) {
    return Math.multiplyExact(annotationCount, annotationEncoder.getEncodedSizeInBytes());
  }
}
