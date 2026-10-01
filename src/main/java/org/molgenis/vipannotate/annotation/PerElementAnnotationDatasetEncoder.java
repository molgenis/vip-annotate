package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class PerElementAnnotationDatasetEncoder<T extends Annotation>
    implements AnnotationDatasetEncoder<T> {
  private final AnnotationEncoder<T> annotationEncoder;

  @Override
  public void encodeInto(SizedIterator<T> annotationIt, MemoryEncoderProvider encoderProvider) {
    MemoryEncoder memEncoder = encoderProvider.get();
    annotationIt.forEachRemaining(value -> annotationEncoder.encodeInto(value, memEncoder));
  }
}
