package org.molgenis.vcf.annotate.db.chrpos.remm;

import org.molgenis.vcf.annotate.db.Quantizer;

public class RemmCodec {
  public static byte encode(double score) {
    return (byte) Quantizer.quantizeToByte(score);
  }

  public static Double decode(byte encodedScore) {
    return Quantizer.dequantizeFromByte(encodedScore & 0xFF);
  }
}
