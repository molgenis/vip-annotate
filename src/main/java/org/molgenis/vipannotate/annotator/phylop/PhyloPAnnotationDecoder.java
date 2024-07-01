package org.molgenis.vipannotate.annotator.phylop;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.exact.format.AnnotationDecoder;

public class PhyloPAnnotationDecoder implements AnnotationDecoder<Double> {
  public static final String ANNOTATION_ID = "phyloP";
  public static final int NR_ANNOTATION_BYTES = 2;

  @Override
  public Double decode(MemoryBuffer memoryBuffer) {
    short encodedScore = memoryBuffer.readInt16();
    if (encodedScore == 0) return null;
    return ((encodedScore - 1) / 1000d) + (-11.726);
  }
}
