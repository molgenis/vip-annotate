package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;

public class SequenceVariantAnnotationIndexBigFactory<T extends SequenceVariant> {
  private SequenceVariantAnnotationIndexBigFactory() {}

  /** create a new empty index */
  public static <T extends SequenceVariant> SequenceVariantAnnotationIndexBig<T> create() {
    // TODO reuse same encoder
    return new SequenceVariantAnnotationIndexBig<>(
        new SequenceVariantEncoderBig<>(), new BigInteger[0]);
  }
}
