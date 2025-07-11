package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader
    implements AnnotationIndexReader<SequenceVariant> {
  private final AnnotationBlobReader annotationBlobReader;
  private final Fury fury;

  /**
   * @return annotation index
   */
  @Override
  public AnnotationIndex<SequenceVariant> read(Partition.Key partitionKey) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    return memoryBuffer != null
        ? fury.deserializeJavaObject(memoryBuffer, SequenceVariantAnnotationIndex.class)
        : EmptyAnnotationIndex.getInstance();
  }

  @Override
  public void close() {
    annotationBlobReader.close();
  }
}
