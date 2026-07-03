package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class ScalarAnnotationDatasetReader implements AnnotationDatasetReader<ScalarAnnotation> {
  private final AnnotationDecoder<ScalarAnnotation> annotationDecoder;
  private final AnnotationBlobReader blobReader;

  @Override
  public AnnotationDataset<ScalarAnnotation> read(PartitionKey partitionKey) {
    MemoryBuffer memoryBuffer = blobReader.read(partitionKey);
    return memoryBuffer != null
        ? new ScalarAnnotationDataset(annotationDecoder, memoryBuffer)
        : EmptyAnnotationDataset.getInstance();
  }

  @Override
  public void close() {
    ClosableUtils.close(blobReader);
  }
}
