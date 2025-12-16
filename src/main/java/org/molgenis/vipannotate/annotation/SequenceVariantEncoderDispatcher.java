package org.molgenis.vipannotate.annotation;

import java.util.EnumMap;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;

public final class SequenceVariantEncoderDispatcher<T extends SequenceVariant>
    implements SequenceVariantEncoder<T> {
  private final EnumMap<Type, SequenceVariantEncoder<T>> encoderMap;

  public SequenceVariantEncoderDispatcher() {
    encoderMap = new EnumMap<>(Type.class);
  }

  public void register(Type type, SequenceVariantEncoder<T> encoder) {
    encoderMap.put(type, encoder);
  }

  @Override
  public EncodedSequenceVariant encode(T variant) {
    return getEncoder(variant).encode(variant);
  }

  @Override
  public void encodeInto(T variant, EncodedSequenceVariant encodedVariant) {
    getEncoder(variant).encodeInto(variant, encodedVariant);
  }

  public SequenceVariantEncoder<T> getEncoder(Type type) {
    SequenceVariantEncoder<T> encoder = encoderMap.get(type);
    if (encoder == null) {
      throw new EnumConstantNotPresentException(Type.class, type.toString());
    }
    return encoder;
  }

  private SequenceVariantEncoder<T> getEncoder(SequenceVariant variant) {
    Type type = SequenceVariantEncoderUtils.determineType(variant);
    return getEncoder(type);
  }
}
