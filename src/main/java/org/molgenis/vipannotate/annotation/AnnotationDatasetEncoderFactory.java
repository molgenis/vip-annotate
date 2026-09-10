package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.*;

@RequiredArgsConstructor
public class AnnotationDatasetEncoderFactory {
  private final FloatAnnotationEncoderFactory floatAnnotationEncoderFactory;
  private final IntAnnotationEncoderFactory intAnnotationEncoderFactory;

  public <T extends Annotation> AnnotationDatasetEncoder<T> createAnnotationDatasetEncoder(
      ResolvedAnnotationSpec annotationSpec) {
    // FIXME get rid of cast
    return (AnnotationDatasetEncoder<T>)
        switch (annotationSpec) {
          case ResolvedEnumAnnotationSpec enumAnnotationSpec ->
              createEnumDataSetEncoder(enumAnnotationSpec);
          case ResolvedEnumSetAnnotationSpec enumSetAnnotationSpec ->
              createEnumSetDataSetEncoder(enumSetAnnotationSpec);
          case ResolvedFloatAnnotationSpec floatAnnotationSpec ->
              createFloatDataSetEncoder(floatAnnotationSpec);
          case ResolvedIntAnnotationSpec intAnnotationSpec ->
              createIntDataSetEncoder(intAnnotationSpec);
        };
  }

  private static EnumAnnotationDatasetEncoder createEnumDataSetEncoder(
      ResolvedEnumAnnotationSpec annotationSpec) {
    return new EnumAnnotationDatasetEncoder(annotationSpec.values(), annotationSpec.nullable());
  }

  private static EnumSetAnnotationDatasetEncoder createEnumSetDataSetEncoder(
      ResolvedEnumSetAnnotationSpec annotationSpec) {
    String[] enumValues = annotationSpec.values();
    return new EnumSetAnnotationDatasetEncoder(enumValues);
  }

  private PerElementAnnotationDatasetEncoder<?> createFloatDataSetEncoder(
      ResolvedFloatAnnotationSpec annotationSpec) {
    return new PerElementAnnotationDatasetEncoder<>(
        floatAnnotationEncoderFactory.create(annotationSpec));
  }

  private PerElementAnnotationDatasetEncoder<?> createIntDataSetEncoder(
      ResolvedIntAnnotationSpec annotationSpec) {
    return new PerElementAnnotationDatasetEncoder<>(
        intAnnotationEncoderFactory.create(annotationSpec));
  }

  public static AnnotationDatasetEncoderFactory create() {
    ValueWriterFactory valueWriterFactory = new ValueWriterFactory();
    FloatAnnotationEncoderFactory floatAnnotationEncoderFactory =
        new FloatAnnotationEncoderFactory(valueWriterFactory);
    IntAnnotationEncoderFactory intAnnotationEncoderFactory =
        new IntAnnotationEncoderFactory(valueWriterFactory);
    return new AnnotationDatasetEncoderFactory(
        floatAnnotationEncoderFactory, intAnnotationEncoderFactory);
  }
}
