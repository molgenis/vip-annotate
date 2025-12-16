package org.molgenis.vipannotate.annotation;

public class SequenceVariantEncoderSmall<T extends SequenceVariant>
    implements SequenceVariantEncoder<T> {
  @Override
  public EncodedSequenceVariant encode(T variant) {
    int encodedVariantValue = encodeAsInt(variant);
    return EncodedSequenceVariant.createSmall(encodedVariantValue);
  }

  @Override
  public void encodeInto(T variant, EncodedSequenceVariant encodedVariant) {
    int encodedVariantValue = encodeAsInt(variant);
    encodedVariant.resetSmall(encodedVariantValue);
  }

  /**
   * Returns an encoded variant for variants with [1,16] reference bases and [1,4] alternate bases
   *
   * <ul>
   *   <li>{@code 18 bits} encoded position
   *   <li>{@code 04 bits} encoded ref length
   *   <li>{@code 02 bits} encoded alt length
   *   <li>{@code 08 bits} encoded alt
   * </ul>
   *
   * @return encoded variant
   */
  private static int encodeAsInt(SequenceVariant variant) {
    CharSequence altBases = variant.getAlt().get();
    int encodedPos = SequenceVariantEncoderUtils.encodePos(variant.getStart());
    int encodedRefLength = SequenceVariantEncoderUtils.encodeBaseCount(variant.getRefLength());
    int encodedAltLength = SequenceVariantEncoderUtils.encodeBaseCount(altBases.length());
    int encodedAlt = encodeAlt(altBases);
    return encodedPos << 14 | encodedRefLength << 10 | encodedAltLength << 8 | encodedAlt;
  }

  /**
   * encoding:
   *
   * <ul>
   *   <li>{@code 2 bits} = alternate base 4
   *   <li>{@code 2 bits} = alternate base 3
   *   <li>{@code 2 bits} = alternate base 2
   *   <li>{@code 2 bits} = alternate base 1
   * </ul>
   *
   * @param altBases [1,4] alternate bases (each base must be one of [A, C, G, T])
   * @return alternate alt encoded in 8 bits
   */
  private static int encodeAlt(CharSequence altBases) {
    int encodedAlt = 0;
    for (int i = 0, length = altBases.length(); i < length; i++) {
      int encodedAltBase = SequenceVariantEncoderUtils.encodeActgBase(altBases.charAt(i));
      encodedAlt |= encodedAltBase << (i * 2);
    }
    return encodedAlt;
  }
}
