package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;

@RequiredArgsConstructor
public class GnomAdShortVariantAnnotationDatasetReader
    implements AnnotationDatasetReader<GnomAdShortVariantAnnotationData> {
  @NonNull
  private final GnomAdShortVariantAnnotationDatasetFactory
      gnomAdShortVariantAnnotationDatasetFactory;

  @NonNull private final AnnotationBlobReader sourceAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader afAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader faf95AnnotationBlobReader;
  @NonNull private final AnnotationBlobReader faf99AnnotationBlobReader;
  @NonNull private final AnnotationBlobReader hnAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader filtersAnnotationBlobReader;
  @NonNull private final AnnotationBlobReader covAnnotationBlobReader;

  @Override
  public AnnotationDataset<GnomAdShortVariantAnnotationData> read(GenomePartitionKey key) {
    MemoryBuffer srcMemoryBuffer = sourceAnnotationBlobReader.read(key);
    MemoryBuffer afMemoryBuffer = afAnnotationBlobReader.read(key);
    MemoryBuffer faf95MemoryBuffer = faf95AnnotationBlobReader.read(key);
    MemoryBuffer faf99MemoryBuffer = faf99AnnotationBlobReader.read(key);
    MemoryBuffer hnMemoryBuffer = hnAnnotationBlobReader.read(key);
    MemoryBuffer filtersMemoryBuffer = filtersAnnotationBlobReader.read(key);
    MemoryBuffer covMemoryBuffer = covAnnotationBlobReader.read(key);

    AnnotationDataset<GnomAdShortVariantAnnotationData> annotationDataset;
    if (srcMemoryBuffer != null
        && afMemoryBuffer != null
        && faf95MemoryBuffer != null
        && faf99MemoryBuffer != null
        && hnMemoryBuffer != null
        && filtersMemoryBuffer != null
        && covMemoryBuffer != null) {
      annotationDataset =
          gnomAdShortVariantAnnotationDatasetFactory.create(
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
}
