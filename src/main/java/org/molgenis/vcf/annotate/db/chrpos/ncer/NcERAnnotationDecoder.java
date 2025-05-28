package org.molgenis.vcf.annotate.db.chrpos.ncer;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationDecoder;

public class NcERAnnotationDecoder implements AnnotationDecoder<Double> {
  public static final String ANNOTATION_ID = "ncER";
  public static final int NR_ANNOTATION_BYTES = 2;

  @Override
  public Double decode(MemoryBuffer memoryBuffer) {
    short encodedPerc = memoryBuffer.readInt16();
    return NcERCodec.decode(encodedPerc);
  }
}
