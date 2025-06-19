package org.molgenis.vipannotate.annotation.gnomad;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;

@RequiredArgsConstructor
public class GnomAdAnnotationDatasetReader implements AnnotationDatasetReader<GnomAdAnnotation> {
  @NonNull private final GnomAdAnnotationDatasetFactory gnomAdAnnotationDatasetFactory;
  @NonNull private final AnnotationBlobReader sourceAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader afAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader faf95AnnotationBlobReader;
  @NonNull private final AnnotationBlobReader faf99AnnotationBlobReader;
  @NonNull private final AnnotationBlobReader hnAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader filtersAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader covAnnotationBlobReader;

  @Override
  public AnnotationDataset<GnomAdAnnotation> read(Partition.Key partitionKey) {
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
