package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class SpliceAiAnnotationDatasetFactory {
  private final SpliceAiAnnotationDatasetDecoder annotationDatasetDecoder;

  public SpliceAiAnnotationDataset create(
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
