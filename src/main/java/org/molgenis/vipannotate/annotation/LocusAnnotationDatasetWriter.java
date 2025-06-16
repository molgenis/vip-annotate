package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterable;

@RequiredArgsConstructor
public class LocusAnnotationDatasetWriter<T extends LocusAnnotation<U>, U>
    implements AnnotationDatasetWriter<T> {
  @NonNull private final String annotationDataId;
  @NonNull private final AnnotationDatasetEncoder<T> annotationDatasetEncoder;
  @NonNull private final GenomePartitionDataWriter genomePartitionDataWriter;

  @Override
  public void write(GenomePartitionKey genomePartitionKey, SizedIterable<T> annotations) {
    MemoryBuffer memoryBuffer = annotationDatasetEncoder.encode(annotations.iterator());
    genomePartitionDataWriter.write(genomePartitionKey, annotationDataId, memoryBuffer);
  }
}
