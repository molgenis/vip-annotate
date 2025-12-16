package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;
import org.molgenis.vipannotate.util.IndexRangeFinder;

@Getter(AccessLevel.PACKAGE)
public class SequenceVariantAnnotationIndexSmall<T extends SequenceVariant>
    implements AnnotationIndex<T> {
  private final SequenceVariantEncoder<T> encoder;
  private int[] encodedVariantsArray;
  private int nrEncodedVariants;

  public SequenceVariantAnnotationIndexSmall(
      SequenceVariantEncoder<T> encoder, int[] encodedVariantsArray) {
    this(encoder, encodedVariantsArray, encodedVariantsArray.length);
  }

  public SequenceVariantAnnotationIndexSmall(
      SequenceVariantEncoder<T> encoder, int[] encodedVariantsArray, int nrEncodedVariants) {
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

    int encodedSmallVariant = encoder.encode(variant).getSmall(); // FIXME call encodeInto
    return IndexRangeFinder.findIndexes(
        encodedVariantsArray, 0, nrEncodedVariants, encodedSmallVariant);
  }

  /** clear index */
  @Override
  public void reset() {
    this.nrEncodedVariants = 0;
  }

  void reset(int[] encodedVariantsArray, int nrEncodedVariants) {
    this.encodedVariantsArray = encodedVariantsArray;
    this.nrEncodedVariants = nrEncodedVariants;
  }
}
