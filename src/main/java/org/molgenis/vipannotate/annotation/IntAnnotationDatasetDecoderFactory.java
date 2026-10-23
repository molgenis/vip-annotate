package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;

@RequiredArgsConstructor
public class IntAnnotationDatasetDecoderFactory {
  private final ReadValueFunctionFactory readValueFunctionFactory;

  public AnnotationDatasetDecoder<?> create(IntAnnotationSpec annotationSpec) {
    AnnotationDecoder<?> annotationDecoder = createAnnotationDecoder(annotationSpec);
    AnnotationDatasetDecoder<?> datasetDecoder =
        new PerElementAnnotationDatasetDecoder<>(annotationDecoder);
    if (annotationSpec.nullable()) {
      datasetDecoder = new NullableAnnotationDatasetDecoder<>(datasetDecoder);
    }
    return datasetDecoder;
  }

  private AnnotationDecoder<?> createAnnotationDecoder(IntAnnotationSpec annotationSpec) {
    IntReadValueFunction readValueFunction =
        readValueFunctionFactory.createIntReadValueFunction(annotationSpec.storageType());

    return switch (annotationSpec.intEncoding()) {
      case OffsetIntEncoding offsetIntEncoding ->
          new OffsetIntAnnotationDecoder(readValueFunction, offsetIntEncoding.offset());
      case PlainIntEncoding _ -> new IntAnnotationDecoder(readValueFunction);
    };
  }
}
