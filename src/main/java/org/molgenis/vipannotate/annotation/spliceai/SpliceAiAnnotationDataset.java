package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDataset;

@RequiredArgsConstructor
public class SpliceAiAnnotationDataset implements AnnotationDataset<SpliceAiAnnotation> {
  private final SpliceAiAnnotationDatasetDecoder annotationDataDecoder;
  private final MemoryBuffer geneIdxMemoryBuffer;
  private final MemoryBuffer geneRefMemoryBuffer;
  private final MemoryBuffer dsagMemoryBuffer;
  private final MemoryBuffer dsalMemoryBuffer;
  private final MemoryBuffer dsdgMemoryBuffer;
  private final MemoryBuffer dsdlMemoryBuffer;
  private final MemoryBuffer dpagMemoryBuffer;
  private final MemoryBuffer dpalMemoryBuffer;
  private final MemoryBuffer dpdgMemoryBuffer;
  private final MemoryBuffer dpdlMemoryBuffer;

  @Override
  public SpliceAiAnnotation findByIndex(int index) {
    int ncbiGeneId =
        annotationDataDecoder.decodeGeneIndex(
            geneIdxMemoryBuffer, annotationDataDecoder.decodeGeneRef(geneRefMemoryBuffer, index));
    double dsag = annotationDataDecoder.decodeScore(dsagMemoryBuffer, index);
    double dsal = annotationDataDecoder.decodeScore(dsalMemoryBuffer, index);
    double dsdg = annotationDataDecoder.decodeScore(dsdgMemoryBuffer, index);
    double dsdl = annotationDataDecoder.decodeScore(dsdlMemoryBuffer, index);
    byte dpag = annotationDataDecoder.decodePos(dpagMemoryBuffer, index);
    byte dpal = annotationDataDecoder.decodePos(dpalMemoryBuffer, index);
    byte dpdg = annotationDataDecoder.decodePos(dpdgMemoryBuffer, index);
    byte dpdl = annotationDataDecoder.decodePos(dpdlMemoryBuffer, index);
    return new SpliceAiAnnotation(ncbiGeneId, dsag, dsal, dsdg, dsdl, dpag, dpal, dpdg, dpdl);
  }
}
