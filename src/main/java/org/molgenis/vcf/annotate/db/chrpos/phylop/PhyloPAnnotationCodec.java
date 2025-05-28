package org.molgenis.vcf.annotate.db.chrpos.phylop;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationDecoder;

public class PhyloPAnnotationCodec implements AnnotationDecoder<Double> {
  //  private static final double OFFSET = -11.726;
  //
  //  @Override
  //  public MemoryBuffer encode(Double annotation) {
  //    //    if (annotation == null) throw new IllegalArgumentException("annotation cannot be
  // null");
  //    //    double annotationUnsigned = annotation - OFFSET;
  //    //    short encodedScore = (short) ((annotationUnsigned * 1000) + 1); // reserve 0 for
  // missing
  //    // values
  //    //    return encodedScore
  //
  //    throw new RuntimeException("fixme: implement"); // FIXME implement
  //  }

  @Override
  public Double decode(MemoryBuffer memoryBuffer) {
    short encodedScore = memoryBuffer.readInt16();
    if (encodedScore == 0) return null;
    return ((encodedScore - 1) / 1000d) + (-11.726);
  }
}
