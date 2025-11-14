package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader<T extends SequenceVariant>
    implements AnnotationIndexReader<T> {
  private final AnnotationBlobReader annotationBlobReader;
  private final MemoryBufferReader<AnnotationIndex<T>> indexReader;

  @Override
  public @Nullable AnnotationIndex<T> read(PartitionKey partitionKey) {
    MemoryBuffer memBuffer = annotationBlobReader.read(partitionKey);
    if (memBuffer == null) {
      return null;
    }

    memBuffer.flip();
    return indexReader.readFrom(memBuffer);
  }

  @Override
  public boolean readInto(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex) {
    MemoryBuffer memoryBuffer = annotationBlobReader.read(partitionKey);
    if (memoryBuffer == null) {
      return false;
    }

    memoryBuffer.flip();
    indexReader.readInto(memoryBuffer, annotationIndex);
    return true;
  }

  @Override
  public void close() {
    annotationBlobReader.close();
  }
}
