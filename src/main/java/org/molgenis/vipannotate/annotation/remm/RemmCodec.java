package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.util.Quantizer;

public class RemmCodec {
  public static byte encode(double score) {
    return (byte) Quantizer.quantizeToByte(score);
  }

  public static Double decode(byte encodedScore) {
    return Quantizer.dequantizeFromByte(encodedScore & 0xFF);
  }
}
