package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexReader<T extends SequenceVariant>
    implements AutoCloseableNoThrow {
  private final PartitionedEntryReader entryReader;
  private final MemoryDecodable<SequenceVariantAnnotationIndexDispatcher<T>> indexReader;

  public @Nullable SequenceVariantAnnotationIndexDispatcher<T> read(PartitionKey partitionKey) {
    MemoryDecoder memDecoder = entryReader.read(partitionKey);
    if (memDecoder == null) {
      return null;
    }
    return indexReader.readFrom(memDecoder);
  }

  public boolean readInto(
      PartitionKey partitionKey, SequenceVariantAnnotationIndexDispatcher<T> annotationIndex) {
    MemoryDecoder memDecoder = entryReader.read(partitionKey);
    if (memDecoder == null) {
      return false;
    }
    indexReader.readInto(memDecoder, annotationIndex);
    return true;
  }

  @Override
  public void close() {
    ClosableUtils.close(entryReader);
  }
}
