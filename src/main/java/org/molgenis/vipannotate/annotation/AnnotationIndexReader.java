package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationIndexReader implements AutoCloseable {
  private final AnnotationBlobReader annotationBlobReader;
  private final Fury fury;

  /**
   * @return annotation index
   */
  public AnnotationIndex read(Partition.Key partitionKey) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    return memoryBuffer != null
        ? fury.deserializeJavaObject(memoryBuffer, AnnotationIndexImpl.class)
        : EmptyAnnotationIndex.getInstance();
  }

  @Override
  public void close() {
    annotationBlobReader.close();
  }
}
