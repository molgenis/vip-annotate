package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class ScalarAnnotationDatasetReader implements AnnotationDatasetDecoder<ScalarAnnotation> {
  private final AnnotationDecoder<ScalarAnnotation> annotationDecoder;
  private final AnnotationBlobReader blobReader;

  @Override
  public AnnotationDataset<ScalarAnnotation> decode(PartitionKey partitionKey) {
    BinaryReader binaryReader = blobReader.read(partitionKey);
    return binaryReader != null
        ? new ScalarAnnotationDataset(annotationDecoder, binaryReader)
        : EmptyAnnotationDataset.getInstance();
  }

  @Override
  public void close() {
    ClosableUtils.close(blobReader);
  }
}
