package org.molgenis.vipannotate.annotation.gnomad;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;

@RequiredArgsConstructor
public class GnomAdAnnotationDatasetFactory {
  private final GnomAdAnnotationDatasetDecoder annotationDatasetDecoder;

  public GnomAdAnnotationDataset create(
      MemoryBuffer srcMemoryBuffer,
      MemoryBuffer afMemoryBuffer,
      MemoryBuffer faf95MemoryBuffer,
      MemoryBuffer faf99MemoryBuffer,
      MemoryBuffer hnMemoryBuffer,
      MemoryBuffer filtersMemoryBuffer,
      MemoryBuffer covMemoryBuffer) {
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
