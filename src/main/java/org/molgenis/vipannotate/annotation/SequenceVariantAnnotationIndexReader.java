package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.ClosableUtils;

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
    return indexReader.readFrom(memBuffer);
  }

  @Override
  public boolean readInto(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex) {
    MemoryBuffer memBuffer = annotationBlobReader.read(partitionKey);
    if (memBuffer == null) {
      return false;
    }

    indexReader.readInto(memBuffer, annotationIndex);
    return true;
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationBlobReader);
  }
}
