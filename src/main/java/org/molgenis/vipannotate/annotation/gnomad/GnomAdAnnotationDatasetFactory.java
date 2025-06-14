package org.molgenis.vipannotate.annotation.gnomad;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class GnomAdAnnotationDatasetFactory {
  @NonNull private final GnomAdAnnotationDatasetDecoder annotationDatasetDecoder;

  public GnomAdAnnotationDataset create(
      @NonNull MemoryBuffer srcMemoryBuffer,
      @NonNull MemoryBuffer afMemoryBuffer,
      @NonNull MemoryBuffer faf95MemoryBuffer,
      @NonNull MemoryBuffer faf99MemoryBuffer,
      @NonNull MemoryBuffer hnMemoryBuffer,
      @NonNull MemoryBuffer filtersMemoryBuffer,
      @NonNull MemoryBuffer covMemoryBuffer) {
    return new GnomAdAnnotationDataset(
        annotationDatasetDecoder,
        srcMemoryBuffer,
        afMemoryBuffer,
        faf95MemoryBuffer,
        faf99MemoryBuffer,
        hnMemoryBuffer,
        filtersMemoryBuffer,
        covMemoryBuffer);
  }
}
