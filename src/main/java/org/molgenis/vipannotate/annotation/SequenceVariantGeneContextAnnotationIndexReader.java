package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class SequenceVariantGeneContextAnnotationIndexReader
    implements AnnotationIndexReader<SequenceVariantGeneContext> {
  private final AnnotationBlobReader annotationBlobReader;
  private final Fury fury;

  /**
   * @return annotation index
   */
  @Override
  public AnnotationIndex<SequenceVariantGeneContext> read(Partition.Key partitionKey) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    return memoryBuffer != null
        ? fury.deserializeJavaObject(memoryBuffer, SequenceVariantGeneContextAnnotationIndex.class)
        : EmptyAnnotationIndex.getInstance();
  }

  @Override
  public void close() {
    annotationBlobReader.close();
  }
}
