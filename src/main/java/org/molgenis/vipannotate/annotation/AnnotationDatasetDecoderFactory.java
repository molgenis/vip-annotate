package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;

@RequiredArgsConstructor
public class AnnotationDatasetDecoderFactory {
  private final EnumAnnotationDatasetDecoderFactory enumAnnotationDatasetDecoderFactory;
  private final EnumSetAnnotationDatasetDecoderFactory enumSetAnnotationDatasetDecoderFactory;
  private final FloatAnnotationDatasetDecoderFactory floatAnnotationDatasetDecoderFactory;
  private final IntAnnotationDatasetDecoderFactory intAnnotationDatasetDecoderFactory;

  public AnnotationDatasetDecoder<?> create(
      AnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec enumAnnotationSpec ->
          enumAnnotationDatasetDecoderFactory.create(enumAnnotationSpec, blobReader);
      case EnumSetAnnotationSpec enumSetAnnotationSpec ->
          enumSetAnnotationDatasetDecoderFactory.create(enumSetAnnotationSpec, blobReader);
      case FloatAnnotationSpec floatAnnotationSpec ->
          floatAnnotationDatasetDecoderFactory.create(floatAnnotationSpec, blobReader);
      case IntAnnotationSpec intAnnotationSpec ->
          intAnnotationDatasetDecoderFactory.create(intAnnotationSpec, blobReader);
    };
  }

  public static AnnotationDatasetDecoderFactory create() {
    ReadValueFunctionFactory readValueFunctionFactory = ReadValueFunctionFactory.create();
    return new AnnotationDatasetDecoderFactory(
        new EnumAnnotationDatasetDecoderFactory(),
        new EnumSetAnnotationDatasetDecoderFactory(),
        new FloatAnnotationDatasetDecoderFactory(readValueFunctionFactory),
        new IntAnnotationDatasetDecoderFactory(readValueFunctionFactory));
  }
}
