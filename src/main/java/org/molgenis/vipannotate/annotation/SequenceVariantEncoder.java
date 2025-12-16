package org.molgenis.vipannotate.annotation;

public interface SequenceVariantEncoder<T extends SequenceVariant> {
  EncodedSequenceVariant encode(T variant);

  void encodeInto(T variant, EncodedSequenceVariant encodedVariant);
}
