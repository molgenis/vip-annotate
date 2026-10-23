package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SequenceVariantAnnotationIndexDispatcherWriterFactory<T extends SequenceVariant> {
  private final MemoryBufferFactory memBufferFactory;

  public static <T extends SequenceVariant>
      SequenceVariantAnnotationIndexDispatcherWriterFactory<T> create(
          MemoryBufferFactory memBufferFactory) {
    return new SequenceVariantAnnotationIndexDispatcherWriterFactory<>(memBufferFactory);
  }

  public SequenceVariantAnnotationIndexDispatcherWriter<T> createWriter() {
    SequenceVariantAnnotationIndexDispatcherWriter<T> writer =
        new SequenceVariantAnnotationIndexDispatcherWriter<>(memBufferFactory);
    StreamVByte streamVByte = StreamVByte.create();
    writer.register(
        EncodedSequenceVariant.Type.POS_20_BIT,
        new SequenceVariantAnnotationIndexSmallWriter<>(memBufferFactory, streamVByte));
    writer.register(
        EncodedSequenceVariant.Type.POS_26_BIT,
        new SequenceVariantAnnotationIndexSmallWriter<>(memBufferFactory, streamVByte));
    writer.register(
        EncodedSequenceVariant.Type.BIG,
        new SequenceVariantAnnotationIndexBigWriter<>(memBufferFactory));
    return writer;
  }
}
