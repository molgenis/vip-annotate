package org.molgenis.vipannotate.annotation.gnomad;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class GnomAdAnnotationDatasetReader implements AnnotationDatasetReader<GnomAdAnnotation> {
  private final GnomAdAnnotationDatasetFactory gnomAdAnnotationDatasetFactory;
  private final AnnotationBlobReader sourceAnnotationBlobReader;
  private final AnnotationBlobReader afAnnotationBlobReader;
  private final AnnotationBlobReader faf95AnnotationBlobReader;
  private final AnnotationBlobReader faf99AnnotationBlobReader;
  private final AnnotationBlobReader hnAnnotationBlobReader;
  private final AnnotationBlobReader filtersAnnotationBlobReader;
  private final AnnotationBlobReader covAnnotationBlobReader;

  @Override
  public AnnotationDataset<GnomAdAnnotation> read(PartitionKey partitionKey) {
    MemoryBuffer srcMemoryBuffer = sourceAnnotationBlobReader.read(partitionKey);
    MemoryBuffer afMemoryBuffer = afAnnotationBlobReader.read(partitionKey);
    MemoryBuffer faf95MemoryBuffer = faf95AnnotationBlobReader.read(partitionKey);
    MemoryBuffer faf99MemoryBuffer = faf99AnnotationBlobReader.read(partitionKey);
    MemoryBuffer hnMemoryBuffer = hnAnnotationBlobReader.read(partitionKey);
    MemoryBuffer filtersMemoryBuffer = filtersAnnotationBlobReader.read(partitionKey);
    MemoryBuffer covMemoryBuffer = covAnnotationBlobReader.read(partitionKey);

    AnnotationDataset<GnomAdAnnotation> annotationDataset;
    if (srcMemoryBuffer != null
        && afMemoryBuffer != null
        && faf95MemoryBuffer != null
        && faf99MemoryBuffer != null
        && hnMemoryBuffer != null
        && filtersMemoryBuffer != null
        && covMemoryBuffer != null) {
      annotationDataset =
          gnomAdAnnotationDatasetFactory.create(
              srcMemoryBuffer,
              afMemoryBuffer,
              faf95MemoryBuffer,
              faf99MemoryBuffer,
              hnMemoryBuffer,
              filtersMemoryBuffer,
              covMemoryBuffer);
    } else {
      annotationDataset = EmptyAnnotationDataset.getInstance();
    }
    return annotationDataset;
  }

  @Override
  public void close() {
    sourceAnnotationBlobReader.close();
    afAnnotationBlobReader.close();
    faf95AnnotationBlobReader.close();
    faf99AnnotationBlobReader.close();
    hnAnnotationBlobReader.close();
    filtersAnnotationBlobReader.close();
    covAnnotationBlobReader.close();
  }
}
