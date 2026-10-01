package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader<T extends SequenceVariant>
    implements AnnotationIndexReader<T> {
  private final AnnotationBlobReader annotationBlobReader;
  private final MemoryBufferReader<AnnotationIndex<T>> indexReader;

  @Override
  public @Nullable AnnotationIndex<T> read(PartitionKey partitionKey) {
    BinaryReader binaryReader = annotationBlobReader.read(partitionKey);
    if (binaryReader == null) {
      return null;
    }
    return indexReader.readFrom(binaryReader.unwrap());
  }

  @Override
  public boolean readInto(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex) {
    BinaryReader binaryReader = annotationBlobReader.read(partitionKey);
    if (binaryReader == null) {
      return false;
    }
    indexReader.readInto(binaryReader.unwrap(), annotationIndex);
    return true;
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationBlobReader);
  }
}
