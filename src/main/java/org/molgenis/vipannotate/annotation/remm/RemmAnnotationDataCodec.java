package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.util.Quantizer;

public class RemmAnnotationDataCodec {
  // FIXME remove static to enable upstream unit testing
  public static byte encode(double score) {
    return (byte) Quantizer.quantizeToByte(score);
  }

  public Double decode(byte encodedScore) {
    return Quantizer.dequantizeFromByte(encodedScore & 0xFF);
  }
}
