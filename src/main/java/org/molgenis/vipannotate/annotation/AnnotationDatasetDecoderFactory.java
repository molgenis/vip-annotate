package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.*;

@RequiredArgsConstructor
public class AnnotationDatasetDecoderFactory {
  private final EnumAnnotationDatasetDecoderFactory enumAnnotationDatasetDecoderFactory;
  private final EnumSetAnnotationDatasetDecoderFactory enumSetAnnotationDatasetDecoderFactory;
  private final FloatAnnotationDatasetDecoderFactory floatAnnotationDatasetDecoderFactory;
  private final IntAnnotationDatasetDecoderFactory intAnnotationDatasetDecoderFactory;

  public AnnotationDatasetDecoder<?> create(
      ResolvedAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    return switch (annotationSpec) {
      case ResolvedEnumAnnotationSpec enumAnnotationSpec ->
          enumAnnotationDatasetDecoderFactory.create(enumAnnotationSpec, blobReader);
      case ResolvedEnumSetAnnotationSpec enumSetAnnotationSpec ->
          enumSetAnnotationDatasetDecoderFactory.create(enumSetAnnotationSpec, blobReader);
      case ResolvedFloatAnnotationSpec floatAnnotationSpec ->
          floatAnnotationDatasetDecoderFactory.create(floatAnnotationSpec, blobReader);
      case ResolvedIntAnnotationSpec intAnnotationSpec ->
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
