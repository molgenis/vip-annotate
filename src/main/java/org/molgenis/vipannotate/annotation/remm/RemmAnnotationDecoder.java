package org.molgenis.vipannotate.annotation.remm;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDecoder;

public class RemmAnnotationDecoder implements AnnotationDecoder<Double> {
  public static final String ANNOTATION_ID = "REMM";
  public static final int NR_ANNOTATION_BYTES = 1;

  @Override
  public Double decode(MemoryBuffer memoryBuffer) {
    byte encodedValue = memoryBuffer.readByte();
    return RemmCodec.decode(encodedValue);
  }
}
