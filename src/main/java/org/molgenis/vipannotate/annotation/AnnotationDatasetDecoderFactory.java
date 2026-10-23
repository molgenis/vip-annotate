package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;

@RequiredArgsConstructor
public class AnnotationDatasetDecoderFactory {
  private final EnumAnnotationDatasetDecoderFactory enumAnnotationDatasetDecoderFactory;
  private final EnumSetAnnotationDatasetDecoderFactory enumSetAnnotationDatasetDecoderFactory;
  private final FloatAnnotationDatasetDecoderFactory floatAnnotationDatasetDecoderFactory;
  private final IntAnnotationDatasetDecoderFactory intAnnotationDatasetDecoderFactory;

  public AnnotationDatasetDecoder<?> create(AnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec enumAnnotationSpec ->
          enumAnnotationDatasetDecoderFactory.create(enumAnnotationSpec);
      case EnumSetAnnotationSpec enumSetAnnotationSpec ->
          enumSetAnnotationDatasetDecoderFactory.create(enumSetAnnotationSpec);
      case FloatAnnotationSpec floatAnnotationSpec ->
          floatAnnotationDatasetDecoderFactory.create(floatAnnotationSpec);
      case IntAnnotationSpec intAnnotationSpec ->
          intAnnotationDatasetDecoderFactory.create(intAnnotationSpec);
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
