package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import lombok.AccessLevel;
import lombok.Getter;
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
  public boolean findIndexesInto(T variant, int encodedPos, IndexRange indexRange) {
    if (isEmpty()) {
      return false;
    }

    // FIXME call encodeInto
    // FIXME use big bytes + length instead of BigInteger
    BigInteger encodedVariant = new BigInteger(encoder.encode(variant, encodedPos).getBigBytes());
    return IndexRangeFinder.findIndexesInto(
        encodedVariantsArray, 0, nrEncodedVariants, encodedVariant, indexRange);
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
