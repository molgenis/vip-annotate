package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class FloatAnnotationDatasetDecoderFactory {
  private final ReadValueFunctionFactory readValueFunctionFactory;

  public AnnotationDatasetDecoder<?> create(
      ResolvedFloatAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    AnnotationDecoder<?> annotationDecoder = createAnnotationDecoder(annotationSpec);
    return new PerElementAnnotationDatasetReader<>(annotationDecoder, blobReader);
  }

  private AnnotationDecoder<?> createAnnotationDecoder(ResolvedFloatAnnotationSpec annotationSpec) {
    return switch (annotationSpec.storageType()) {
      case FloatType floatType -> create(annotationSpec, floatType);
      case IntType intType -> create(annotationSpec, intType);
    };
  }

  private AnnotationDecoder<?> create(
      ResolvedFloatAnnotationSpec annotationSpec, FloatType floatType) {
    FloatReadValueFunction readValueFunction =
        readValueFunctionFactory.createFloatReadValueFunction(floatType);

    return switch (annotationSpec.floatEncoding()) {
      case NullableFloatEncoding _ -> new NullableFloatAnnotationDecoder(readValueFunction);
      case PlainFloatEncoding _ -> new FloatAnnotationDecoder(readValueFunction);
      case QuantizedEncoding _ -> throw new IllegalArgumentException(); // FIXME improve error msg
    };
  }

  private AnnotationDecoder<?> create(ResolvedFloatAnnotationSpec annotationSpec, IntType intType) {
    IntReadValueFunction readValueFunction =
        readValueFunctionFactory.createIntReadValueFunction(intType);

    // FIXME get rid of this check
    if (!(annotationSpec.floatEncoding()
        instanceof
        QuantizedEncoding(
            QuantizedEncoding.Range range,
            QuantizedEncoding.Levels levels,
            Integer nullCode))) {
      throw new IllegalArgumentException();
    }

    return new QuantizedAnnotationDecoder(
        new Quantizer(
            new DoubleInterval(range.min(), range.max()),
            new IntInterval(levels.min(), levels.max())),
        readValueFunction,
        nullCode);
  }
}
