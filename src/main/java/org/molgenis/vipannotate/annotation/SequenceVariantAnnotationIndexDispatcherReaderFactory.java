package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SequenceVariantAnnotationIndexDispatcherReaderFactory<T extends SequenceVariant> {
  private final SequenceVariantEncoderDispatcher<T> encoderDispatcher;

  public static <T extends SequenceVariant>
      SequenceVariantAnnotationIndexDispatcherReaderFactory<T> create() {
    SequenceVariantEncoderDispatcher<T> encoderDispatcher =
        SequenceVariantEncoderDispatcherFactory.create();
    return new SequenceVariantAnnotationIndexDispatcherReaderFactory<>(encoderDispatcher);
  }

  public SequenceVariantAnnotationIndexDispatcherReader<T> createReader() {
    SequenceVariantAnnotationIndexDispatcherReader<T> reader =
        new SequenceVariantAnnotationIndexDispatcherReader<>();
    StreamVByte streamVByte = StreamVByte.create();
    reader.register(
        EncodedSequenceVariant.Type.POS_20_BIT,
        new SequenceVariantAnnotationIndexSmallReader<>(
            encoderDispatcher.getEncoder(EncodedSequenceVariant.Type.POS_20_BIT), streamVByte));
    reader.register(
        EncodedSequenceVariant.Type.POS_26_BIT,
        new SequenceVariantAnnotationIndexSmallReader<>(
            encoderDispatcher.getEncoder(EncodedSequenceVariant.Type.POS_26_BIT), streamVByte));
    reader.register(
        EncodedSequenceVariant.Type.BIG,
        new SequenceVariantAnnotationIndexBigReader<>(
            encoderDispatcher.getEncoder(EncodedSequenceVariant.Type.BIG)));
    return reader;
  }
}
