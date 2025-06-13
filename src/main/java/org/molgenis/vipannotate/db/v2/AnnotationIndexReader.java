package org.molgenis.vipannotate.db.v2;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;

// FIXME AutoClosable?
@RequiredArgsConstructor
public class AnnotationIndexReader {
  @NonNull private final AnnotationBlobReader annotationBlobReader;
  @NonNull private final Fury fury;

  /**
   * @return annotation index
   */
  public AnnotationIndex read(GenomePartitionKey genomePartitionKey) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(genomePartitionKey);
    return memoryBuffer != null
        ? fury.deserializeJavaObject(memoryBuffer, AnnotationIndexImpl.class)
        : EmptyAnnotationIndex.getInstance();
  }
}
