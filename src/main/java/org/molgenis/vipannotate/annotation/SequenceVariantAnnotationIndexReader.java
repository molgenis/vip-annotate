package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader<T extends SequenceVariant>
    implements AutoCloseableNoThrow {
  private final AnnotationBlobReader annotationBlobReader;
  private final MemoryBufferReader<SequenceVariantAnnotationIndexDispatcher<T>> indexReader;

  public @Nullable SequenceVariantAnnotationIndexDispatcher<T> read(PartitionKey partitionKey) {
    BinaryReader binaryReader = annotationBlobReader.read(partitionKey);
    if (binaryReader == null) {
      return null;
    }
    return indexReader.readFrom(binaryReader.unwrap());
  }

  public boolean readInto(
      PartitionKey partitionKey, SequenceVariantAnnotationIndexDispatcher<T> annotationIndex) {
    BinaryReader binaryReader = annotationBlobReader.read(partitionKey);
    if (binaryReader == null) {
      return false;
    }
    indexReader.readInto(binaryReader.unwrap(), annotationIndex);
    return true;
  }

  public void close() {
    ClosableUtils.close(annotationBlobReader);
  }
}
