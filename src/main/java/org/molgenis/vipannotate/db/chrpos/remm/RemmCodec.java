package org.molgenis.vipannotate.db.chrpos.remm;

import org.molgenis.vipannotate.db.Quantizer;

public class RemmCodec {
  public static byte encode(double score) {
    return (byte) Quantizer.quantizeToByte(score);
  }

  public static Double decode(byte encodedScore) {
    return Quantizer.dequantizeFromByte(encodedScore & 0xFF);
  }
}
