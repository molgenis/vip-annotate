package org.molgenis.vipannotate.db.chrpos.remm;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.exact.format.AnnotationDecoder;

public class RemmAnnotationDecoder implements AnnotationDecoder<Double> {
  public static final String ANNOTATION_ID = "REMM";
  public static final int NR_ANNOTATION_BYTES = 1;

  @Override
  public Double decode(MemoryBuffer memoryBuffer) {
    byte encodedValue = memoryBuffer.readByte();
    return RemmCodec.decode(encodedValue);
  }
}
