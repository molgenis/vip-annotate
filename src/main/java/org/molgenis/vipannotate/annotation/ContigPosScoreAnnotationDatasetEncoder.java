package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<IndexedLocusAnnotation<ContigPosAnnotation, Double>> {
  @NonNull private final AnnotationEncoder<ContigPosAnnotation> annotationEncoder;
  @NonNull private final MemoryBuffer reusableMemoryBuffer;

  @Override
  public MemoryBuffer encode(
      SizedIterator<IndexedLocusAnnotation<ContigPosAnnotation, Double>> annotationIterator,
      int maxAnnotations) {
    annotationEncoder.clear(0, maxAnnotations, reusableMemoryBuffer);

    annotationIterator.forEachRemaining(
        indexedLocusAnnotation ->
            annotationEncoder.encode(
                indexedLocusAnnotation.index(),
                indexedLocusAnnotation.annotation(),
                reusableMemoryBuffer));

    return reusableMemoryBuffer.slice(
        0, maxAnnotations * annotationEncoder.getAnnotationSizeInBytes());
  }
}
