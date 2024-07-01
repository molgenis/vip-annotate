package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader<T extends SequenceVariant>
    implements AnnotationIndexReader<T> {
  private final AnnotationBlobReader annotationBlobReader;
  private final BinarySerializer<AnnotationIndex<T>> indexSerializer;

  @Override
  public AnnotationIndex<T> read(PartitionKey partitionKey) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    return memoryBuffer != null
        ? indexSerializer.readFrom(memoryBuffer)
        : indexSerializer.readEmpty();
  }

  @Override
  public void readInto(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    if (memoryBuffer != null) {
      indexSerializer.readInto(memoryBuffer, annotationIndex);
    } else {
      annotationIndex.reset();
    }
  }

  @Override
  public void close() {
    annotationBlobReader.close();
  }
}
