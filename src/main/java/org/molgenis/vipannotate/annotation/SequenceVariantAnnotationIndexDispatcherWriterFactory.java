package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SequenceVariantAnnotationIndexDispatcherWriterFactory<T extends SequenceVariant> {
  private final MemoryBufferFactory memBufferFactory;
  private final SequenceVariantEncoderDispatcher<T> encoderDispatcher; // TODO why is this unused?

  public static <T extends SequenceVariant>
      SequenceVariantAnnotationIndexDispatcherWriterFactory<T> create(
          MemoryBufferFactory memBufferFactory) {
    SequenceVariantEncoderDispatcher<T> encoderDispatcher =
        SequenceVariantEncoderDispatcherFactory.create();
    return new SequenceVariantAnnotationIndexDispatcherWriterFactory<>(
        memBufferFactory, encoderDispatcher);
  }

  public SequenceVariantAnnotationIndexDispatcherWriter<T> createWriter() {
    SequenceVariantAnnotationIndexDispatcherWriter<T> writer =
        new SequenceVariantAnnotationIndexDispatcherWriter<>(memBufferFactory);
    writer.register(
        EncodedSequenceVariant.Type.SMALL,
        new SequenceVariantAnnotationIndexSmallWriter<>(memBufferFactory, StreamVByte.create()));
    writer.register(
        EncodedSequenceVariant.Type.BIG,
        new SequenceVariantAnnotationIndexBigWriter<>(memBufferFactory));
    return writer;
  }
}
