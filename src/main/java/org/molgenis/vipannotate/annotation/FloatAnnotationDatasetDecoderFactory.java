package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class FloatAnnotationDatasetDecoderFactory {
  private final ReadValueFunctionFactory readValueFunctionFactory;

  public AnnotationDatasetDecoder<?> create(FloatAnnotationSpec annotationSpec) {
    AnnotationDecoder<?> annotationDecoder = createAnnotationDecoder(annotationSpec);
    AnnotationDatasetDecoder<?> datasetDecoder =
        new PerElementAnnotationDatasetDecoder<>(annotationDecoder);
    if (annotationSpec.nullable()) {
      datasetDecoder = new NullableAnnotationDatasetDecoder<>(datasetDecoder);
    }
    return datasetDecoder;
  }

  private AnnotationDecoder<?> createAnnotationDecoder(FloatAnnotationSpec annotationSpec) {
    return switch (annotationSpec.storageType()) {
      case FloatType floatType -> create(annotationSpec, floatType);
      case IntType intType -> create(annotationSpec, intType);
    };
  }

  private AnnotationDecoder<?> create(FloatAnnotationSpec annotationSpec, FloatType floatType) {
    FloatReadValueFunction readValueFunction =
        readValueFunctionFactory.createFloatReadValueFunction(floatType);

    return switch (annotationSpec.floatEncoding()) {
      case PlainFloatEncoding _ -> new FloatAnnotationDecoder(readValueFunction);
      case QuantizedEncoding _ -> throw new IllegalArgumentException();
    };
  }

  private AnnotationDecoder<?> create(FloatAnnotationSpec annotationSpec, IntType intType) {
    IntReadValueFunction readValueFunction =
        readValueFunctionFactory.createIntReadValueFunction(intType);

    return switch (annotationSpec.floatEncoding()) {
      case PlainFloatEncoding _ -> throw new IllegalArgumentException();
      case QuantizedEncoding quantizedEncoding ->
          new QuantizedAnnotationDecoder(
              new Quantizer(
                  new DoubleInterval(
                      quantizedEncoding.range().min(), quantizedEncoding.range().max()),
                  new IntInterval(
                      quantizedEncoding.levels().min(), quantizedEncoding.levels().max())),
              readValueFunction);
    };
  }
}
