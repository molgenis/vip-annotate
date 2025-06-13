package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class GnomAdShortVariantAnnotationDatasetFactory {
  @NonNull private final GnomAdShortVariantAnnotationDatasetDecoder annotationDataDecoder;

  public GnomAdShortVariantAnnotationDataset create(
      @NonNull MemoryBuffer srcMemoryBuffer,
      @NonNull MemoryBuffer afMemoryBuffer,
      @NonNull MemoryBuffer faf95MemoryBuffer,
      @NonNull MemoryBuffer faf99MemoryBuffer,
      @NonNull MemoryBuffer hnMemoryBuffer,
      @NonNull MemoryBuffer filtersMemoryBuffer,
      @NonNull MemoryBuffer covMemoryBuffer) {
    return new GnomAdShortVariantAnnotationDataset(
        annotationDataDecoder,
        srcMemoryBuffer,
        afMemoryBuffer,
        faf95MemoryBuffer,
        faf99MemoryBuffer,
        hnMemoryBuffer,
        filtersMemoryBuffer,
        covMemoryBuffer);
  }
}
