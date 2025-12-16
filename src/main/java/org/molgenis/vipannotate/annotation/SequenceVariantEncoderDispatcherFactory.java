package org.molgenis.vipannotate.annotation;

public class SequenceVariantEncoderDispatcherFactory {
  private SequenceVariantEncoderDispatcherFactory() {}

  public static <T extends SequenceVariant> SequenceVariantEncoderDispatcher<T> create() {
    SequenceVariantEncoderDispatcher<T> dispatcher = new SequenceVariantEncoderDispatcher<>();
    dispatcher.register(EncodedSequenceVariant.Type.SMALL, new SequenceVariantEncoderSmall<>());
    dispatcher.register(EncodedSequenceVariant.Type.BIG, new SequenceVariantEncoderBig<>());
    return dispatcher;
  }
}
