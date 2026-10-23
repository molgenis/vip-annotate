package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;

public final class SequenceVariantEncoderDispatcher<T extends SequenceVariant> {
  private final EnumMap<EncodedSequenceVariant.Type, SequenceVariantEncoder<T>> encoderMap;

  public SequenceVariantEncoderDispatcher() {
    encoderMap = new EnumMap<>(EncodedSequenceVariant.Type.class);
  }

  public void register(EncodedSequenceVariant.Type type, SequenceVariantEncoder<T> encoder) {
    encoderMap.put(type, encoder);
  }

  public EncodedSequenceVariant encode(T variant, PositionEncoding positionEncoding) {
    return getEncoder(variant, positionEncoding.bits()).encode(variant, positionEncoding.value());
  }

  public void encodeInto(
      T variant, PositionEncoding positionEncoding, EncodedSequenceVariant encodedVariant) {
    getEncoder(variant, positionEncoding.bits())
        .encodeInto(variant, positionEncoding.value(), encodedVariant);
  }

  public SequenceVariantEncoder<T> getEncoder(EncodedSequenceVariant.Type type) {
    SequenceVariantEncoder<T> encoder = encoderMap.get(type);
    if (encoder == null) {
      throw new EnumConstantNotPresentException(EncodedSequenceVariant.Type.class, type.toString());
    }
    return encoder;
  }

  private SequenceVariantEncoder<T> getEncoder(SequenceVariant variant, int bits) {
    EncodedSequenceVariant.Type type = SequenceVariantEncoderUtils.determineType(variant, bits);
    return getEncoder(type);
  }
}
