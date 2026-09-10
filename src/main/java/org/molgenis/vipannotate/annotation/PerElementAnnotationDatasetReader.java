package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class PerElementAnnotationDatasetReader<T extends Annotation>
    implements AnnotationDatasetDecoder<T> {
  private final AnnotationDecoder<T> annotationDecoder;
  private final AnnotationBlobReader blobReader;

  @Override
  public AnnotationDataset<T> decode(PartitionKey partitionKey) {
    BinaryReader binaryReader = blobReader.read(partitionKey);
    return binaryReader != null
        ? new PerElementAnnotationDataset<>(annotationDecoder, binaryReader)
        : EmptyAnnotationDataset.getInstance();
  }

  @Override
  public void close() {
    ClosableUtils.close(blobReader);
  }
}
