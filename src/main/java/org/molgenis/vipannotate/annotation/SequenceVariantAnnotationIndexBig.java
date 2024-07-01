package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;
import org.molgenis.vipannotate.util.IndexRangeFinder;

@Getter(AccessLevel.PACKAGE)
public class SequenceVariantAnnotationIndexBig<T extends SequenceVariant>
    implements AnnotationIndex<T> {
  private final SequenceVariantEncoder<T> encoder;
  private BigInteger[] encodedVariantsArray;
  private int nrEncodedVariants;

  public SequenceVariantAnnotationIndexBig(
      SequenceVariantEncoder<T> encoder, BigInteger[] encodedVariantsArray) {
    this(encoder, encodedVariantsArray, encodedVariantsArray.length);
  }

  public SequenceVariantAnnotationIndexBig(
      SequenceVariantEncoder<T> encoder, BigInteger[] encodedVariantsArray, int nrEncodedVariants) {
    this.encoder = encoder;
    this.encodedVariantsArray = encodedVariantsArray;
    this.nrEncodedVariants = nrEncodedVariants;
  }

  @Override
  public boolean isEmpty() {
    return nrEncodedVariants == 0;
  }

  @Override
  public @Nullable IndexRange findIndexes(T variant) {
    if (isEmpty()) {
      return null;
    }

    // FIXME call encodeInto
    // FIXME use big bytes + length instead of BigInteger
    BigInteger encodedVariant = new BigInteger(encoder.encode(variant).getBigBytes());
    return IndexRangeFinder.findIndexes(encodedVariantsArray, 0, nrEncodedVariants, encodedVariant);
  }

  /** clear index */
  @Override
  public void reset() {
    this.nrEncodedVariants = 0;
  }

  void reset(BigInteger[] encodedVariantsArray, int nrEncodedVariants) {
    this.encodedVariantsArray = encodedVariantsArray;
    this.nrEncodedVariants = nrEncodedVariants;
  }
}
