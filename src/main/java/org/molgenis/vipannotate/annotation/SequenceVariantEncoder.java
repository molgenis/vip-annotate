package org.molgenis.vipannotate.annotation;

public interface SequenceVariantEncoder<T extends SequenceVariant> {
  EncodedSequenceVariant encode(T variant, int encodedStartPos);

  void encodeInto(T variant, int encodedStartPos, EncodedSequenceVariant encodedVariant);
}
