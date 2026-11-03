package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SingleAnnotationDatasetReader<T extends Annotation>
    implements AnnotationDatasetReader<T> {
  private final AnnotationDatasetDecoder<T> datasetDecoder;
  private final PartitionedEntryReader entryReader;

  @Override
  public AnnotationDataset<T> read(PartitionKey partitionKey) {
    return datasetDecoder.decode(() -> read(partitionKey, entryReader));
  }

  private static @Nullable MemoryDecoder read(
      PartitionKey partitionKey, PartitionedEntryReader entryReader) {
    return entryReader.read(partitionKey);
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(entryReader);
  }
}
