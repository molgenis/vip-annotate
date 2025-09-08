package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fory.Fory;
import org.apache.fory.memory.MemoryBuffer;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader
    implements AnnotationIndexReader<SequenceVariant> {
  private final AnnotationBlobReader annotationBlobReader;
  private final Fory fory;

  /**
   * @return annotation index
   */
  @Override
  public AnnotationIndex<SequenceVariant> read(Partition.Key partitionKey) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    return memoryBuffer != null
        ? fory.deserializeJavaObject(memoryBuffer, SequenceVariantAnnotationIndex.class)
        : EmptyAnnotationIndex.getInstance();
  }

  @Override
  public void close() {
    annotationBlobReader.close();
  }
}
