package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<IndexedIntervalAnnotation<ContigPosAnnotation, Double>> {
  @NonNull private final AnnotationEncoder<ContigPosAnnotation> annotationEncoder;
  @NonNull private final MemoryBuffer reusableMemoryBuffer;

  @Override
  public MemoryBuffer encode(
      SizedIterator<IndexedIntervalAnnotation<ContigPosAnnotation, Double>> annotationIterator,
      int maxAnnotations) {
    annotationEncoder.clear(0, maxAnnotations, reusableMemoryBuffer);

    annotationIterator.forEachRemaining(
        indexedIntervalAnnotation ->
            annotationEncoder.encode(
                indexedIntervalAnnotation.index(),
                indexedIntervalAnnotation.annotation(),
                reusableMemoryBuffer));

    return reusableMemoryBuffer.slice(
        0, maxAnnotations * annotationEncoder.getAnnotationSizeInBytes());
  }
}
