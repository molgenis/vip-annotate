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
    MemoryDecoder memDecoder = entryReader.read(partitionKey);
    if (memDecoder != null) {
      memDecoder.unwrap().flip(); // FIXME should not happen here (requires IndexReader fix?)
    }
    return memDecoder;
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(entryReader);
  }
}
