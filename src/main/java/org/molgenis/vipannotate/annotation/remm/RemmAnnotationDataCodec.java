package org.molgenis.vipannotate.annotation.remm;

public class RemmAnnotationDataCodec {
  // FIXME remove static to enable upstream unit testing
  public static byte encode(double score) {
    throw new RuntimeException("Not implemented"); // FIXME quantize byte nullable double
  }

  public Double decode(byte encodedScore) {
    throw new RuntimeException("Not implemented"); // FIXME
  }
}
