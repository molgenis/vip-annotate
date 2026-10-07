package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type.*;

public class SequenceVariantEncoderDispatcherFactory {
  private SequenceVariantEncoderDispatcherFactory() {}

  public static <T extends SequenceVariant> SequenceVariantEncoderDispatcher<T> create() {
    SequenceVariantEncoderDispatcher<T> dispatcher = new SequenceVariantEncoderDispatcher<>();
    dispatcher.register(POS_20_BIT, new SequenceVariantEncoderPos20Bit<>());
    dispatcher.register(POS_26_BIT, new SequenceVariantEncoderPos20Bit<>());
    dispatcher.register(BIG, new SequenceVariantEncoderBig<>());
    return dispatcher;
  }
}
