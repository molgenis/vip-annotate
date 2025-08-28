package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;

@RequiredArgsConstructor
public class SpliceAiAnnotationDatasetReader
    implements AnnotationDatasetReader<SpliceAiAnnotation> {
  private final SpliceAiAnnotationDatasetFactory spliceAiAnnotationDatasetFactory;
  private final AnnotationBlobReader geneIdxAnnotationBlobReader;
  private final AnnotationBlobReader geneRefAnnotationBlobReader;
  private final AnnotationBlobReader dsagAnnotationBlobReader;
  private final AnnotationBlobReader dsalAnnotationBlobReader;
  private final AnnotationBlobReader dsdgAnnotationBlobReader;
  private final AnnotationBlobReader dsdlAnnotationBlobReader;
  private final AnnotationBlobReader dpagAnnotationBlobReader;
  private final AnnotationBlobReader dpalAnnotationBlobReader;
  private final AnnotationBlobReader dpdgAnnotationBlobReader;
  private final AnnotationBlobReader dpdlAnnotationBlobReader;

  @Override
  public AnnotationDataset<SpliceAiAnnotation> read(Partition.Key partitionKey) {
    MemoryBuffer geneIdxMemoryBuffer = geneIdxAnnotationBlobReader.read(partitionKey);
    MemoryBuffer geneRefMemoryBuffer = geneRefAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dsagMemoryBuffer = dsagAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dsalMemoryBuffer = dsalAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dsdgMemoryBuffer = dsdgAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dsdlMemoryBuffer = dsdlAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dpagMemoryBuffer = dpagAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dpalMemoryBuffer = dpalAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dpdgMemoryBuffer = dpdgAnnotationBlobReader.read(partitionKey);
    MemoryBuffer dpdlMemoryBuffer = dpdlAnnotationBlobReader.read(partitionKey);

    AnnotationDataset<SpliceAiAnnotation> annotationDataset;
    if (geneIdxMemoryBuffer != null
        && geneRefMemoryBuffer != null
        && dsagMemoryBuffer != null
        && dsalMemoryBuffer != null
        && dsdgMemoryBuffer != null
        && dsdlMemoryBuffer != null
        && dpagMemoryBuffer != null
        && dpalMemoryBuffer != null
        && dpdgMemoryBuffer != null
        && dpdlMemoryBuffer
            != null) { // TODO geneIdxMemoryBuffer and geneRefMemoryBuffer could be null?
      annotationDataset =
          spliceAiAnnotationDatasetFactory.create(
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
    } else {
      annotationDataset = EmptyAnnotationDataset.getInstance();
    }
    return annotationDataset;
  }

  @Override
  public void close() {
    geneIdxAnnotationBlobReader.close();
    geneRefAnnotationBlobReader.close();
    dsagAnnotationBlobReader.close();
    dsalAnnotationBlobReader.close();
    dsdgAnnotationBlobReader.close();
    dsdlAnnotationBlobReader.close();
    dpagAnnotationBlobReader.close();
    dpalAnnotationBlobReader.close();
    dpdgAnnotationBlobReader.close();
    dpdlAnnotationBlobReader.close();
  }
}
