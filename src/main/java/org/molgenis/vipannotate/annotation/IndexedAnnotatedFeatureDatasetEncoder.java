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

  public long calcEncodedSize(int maxAnnotations) {
    return (long) maxAnnotations * annotationEncoder.getAnnotationSizeInBytes();
  }

  @Override
  public void encode(
      SizedIterator<IndexedAnnotation<T>> annotationIt,
      int maxAnnotations,
      MemoryBuffer memBuffer) {
    annotationEncoder.clear(new IndexRange(0, maxAnnotations - 1), memBuffer);

    annotationIt.forEachRemaining(
        indexedIntervalAnnotation ->
            annotationEncoder.encodeInto(indexedIntervalAnnotation, memBuffer));

    long limit = (long) maxAnnotations * annotationEncoder.getAnnotationSizeInBytes();
    memBuffer.setPosition(limit);
    memBuffer.setLimit(limit);
  }
}
