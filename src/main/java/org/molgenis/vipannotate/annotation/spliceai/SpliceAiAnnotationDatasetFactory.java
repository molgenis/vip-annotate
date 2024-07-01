package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class SpliceAiAnnotationDatasetFactory {
  private final SpliceAiAnnotationDatasetDecoder annotationDatasetDecoder;

  public SpliceAiAnnotationDataset create(
      MemoryBuffer geneIdxMemoryBuffer,
      MemoryBuffer geneRefMemoryBuffer,
      MemoryBuffer dsagMemoryBuffer,
      MemoryBuffer dsalMemoryBuffer,
      MemoryBuffer dsdgMemoryBuffer,
      MemoryBuffer dsdlMemoryBuffer,
      MemoryBuffer dpagMemoryBuffer,
      MemoryBuffer dpalMemoryBuffer,
      MemoryBuffer dpdgMemoryBuffer,
      MemoryBuffer dpdlMemoryBuffer) {
    return new SpliceAiAnnotationDataset(
        annotationDatasetDecoder,
        geneIdxMemoryBuffer,
        geneRefMemoryBuffer,
        dsagMemoryBuffer,
        dsalMemoryBuffer,
        dsdgMemoryBuffer,
        dsdlMemoryBuffer,
        dpagMemoryBuffer,
        dpalMemoryBuffer,
        dpdgMemoryBuffer,
        dpdlMemoryBuffer);
  }
}
