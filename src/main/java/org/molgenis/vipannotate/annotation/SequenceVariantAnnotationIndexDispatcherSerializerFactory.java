package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SequenceVariantAnnotationIndexDispatcherSerializerFactory<T extends SequenceVariant> {
  private final SequenceVariantEncoderDispatcher<T> encoderDispatcher;

  public static <T extends SequenceVariant>
      SequenceVariantAnnotationIndexDispatcherSerializerFactory<T> create() {
    SequenceVariantEncoderDispatcher<T> encoderDispatcher =
        SequenceVariantEncoderDispatcherFactory.create();
    return new SequenceVariantAnnotationIndexDispatcherSerializerFactory<>(encoderDispatcher);
  }

  public SequenceVariantAnnotationIndexDispatcherSerializer<T> createSerializer() {
    SequenceVariantAnnotationIndexDispatcherSerializer<T> serializer =
        new SequenceVariantAnnotationIndexDispatcherSerializer<>();
    serializer.register(
        EncodedSequenceVariant.Type.SMALL,
        new SequenceVariantAnnotationIndexSmallSerializer<>(
            encoderDispatcher.getEncoder(EncodedSequenceVariant.Type.SMALL), StreamVByte.create()));
    serializer.register(
        EncodedSequenceVariant.Type.BIG,
        new SequenceVariantAnnotationIndexBigSerializer<>(
            encoderDispatcher.getEncoder(EncodedSequenceVariant.Type.BIG)));
    return serializer;
  }
}
