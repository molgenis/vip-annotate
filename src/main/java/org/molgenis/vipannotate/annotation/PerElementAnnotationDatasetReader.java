package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class PerElementAnnotationDatasetReader<T extends Annotation>
    implements AnnotationDatasetDecoder<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final AnnotationBlobReader blobReader;

  @Override
  public AnnotationDataset<T> decode(PartitionKey partitionKey) {
    MemoryBuffer memoryBuffer = blobReader.read(partitionKey);
    return memoryBuffer != null
        ? new PerElementAnnotationDataset(annotationDecoder, memoryBuffer)
        : EmptyAnnotationDataset.getInstance();
  }

  @Override
  public void close() {
    ClosableUtils.close(blobReader);
  }
}
