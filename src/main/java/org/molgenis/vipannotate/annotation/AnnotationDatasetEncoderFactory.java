package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;

@RequiredArgsConstructor
public class AnnotationDatasetEncoderFactory {
  private final FloatAnnotationEncoderFactory floatAnnotationEncoderFactory;
  private final IntAnnotationEncoderFactory intAnnotationEncoderFactory;

  public <T extends Annotation> AnnotationDatasetEncoder<T> createAnnotationDatasetEncoder(
      AnnotationSpec annotationSpec) {
    // FIXME get rid of cast
    return (AnnotationDatasetEncoder<T>)
        switch (annotationSpec) {
          case EnumAnnotationSpec enumAnnotationSpec ->
              createEnumDataSetEncoder(enumAnnotationSpec);
          case EnumSetAnnotationSpec enumSetAnnotationSpec ->
              createEnumSetDataSetEncoder(enumSetAnnotationSpec);
          case FloatAnnotationSpec floatAnnotationSpec ->
              createFloatDataSetEncoder(floatAnnotationSpec);
          case IntAnnotationSpec intAnnotationSpec -> createIntDataSetEncoder(intAnnotationSpec);
        };
  }

  private static EnumAnnotationDatasetEncoder createEnumDataSetEncoder(
      EnumAnnotationSpec annotationSpec) {
    return new EnumAnnotationDatasetEncoder(annotationSpec.values(), annotationSpec.nullable());
  }

  private static EnumSetAnnotationDatasetEncoder createEnumSetDataSetEncoder(
      EnumSetAnnotationSpec annotationSpec) {
    String[] enumValues = annotationSpec.values();
    return new EnumSetAnnotationDatasetEncoder(enumValues);
  }

  private PerElementAnnotationDatasetEncoder<?> createFloatDataSetEncoder(
      FloatAnnotationSpec annotationSpec) {
    return new PerElementAnnotationDatasetEncoder<>(
        floatAnnotationEncoderFactory.create(annotationSpec));
  }

  private PerElementAnnotationDatasetEncoder<?> createIntDataSetEncoder(
      IntAnnotationSpec annotationSpec) {
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
