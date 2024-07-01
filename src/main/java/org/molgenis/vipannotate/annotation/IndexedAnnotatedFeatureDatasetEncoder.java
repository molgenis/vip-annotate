package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.IndexRange;
import org.molgenis.vipannotate.util.SizedIterator;

/**
 * Encodes annotated genomic feature datasets with an index
 *
 * @param <T> type of genomic feature annotation
 */
@RequiredArgsConstructor
public class IndexedAnnotatedFeatureDatasetEncoder<T extends Annotation>
    implements AnnotationDatasetEncoder<IndexedAnnotation<T>> {
  private final IndexedAnnotationEncoder<T> annotationEncoder;
  private final MemoryBuffer reusableMemoryBuffer;

  @Override
  public MemoryBuffer encode(SizedIterator<IndexedAnnotation<T>> annotationIt, int maxAnnotations) {
    annotationEncoder.clear(new IndexRange(0, maxAnnotations - 1), reusableMemoryBuffer);

    annotationIt.forEachRemaining(
        indexedIntervalAnnotation ->
            annotationEncoder.encode(indexedIntervalAnnotation, reusableMemoryBuffer));
    reusableMemoryBuffer.setPosition(
        (long) maxAnnotations * annotationEncoder.getAnnotationSizeInBytes());
    return reusableMemoryBuffer;
  }
}
