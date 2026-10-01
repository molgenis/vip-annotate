package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinaryWriter;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class PerElementAnnotationDatasetEncoder<T extends Annotation>
    implements AnnotationDatasetEncoder<T> {
  private final AnnotationEncoder<T> annotationEncoder;

  @Override
  public void encode(SizedIterator<T> annotationIt, BinaryWriter binaryWriter) {
    annotationIt.forEachRemaining(value -> annotationEncoder.encodeInto(value, binaryWriter));
  }

  @Override
  public long getEncodedSizeInBytes(int annotationCount) {
    return Math.multiplyExact(annotationCount, annotationEncoder.getEncodedSizeInBytes());
  }
}
