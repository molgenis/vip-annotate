package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.*;

@RequiredArgsConstructor
public class IntAnnotationDatasetDecoderFactory {
  private final ReadValueFunctionFactory readValueFunctionFactory;

  public AnnotationDatasetDecoder<?> create(
      ResolvedIntAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    AnnotationDecoder<?> annotationDecoder = createAnnotationDecoder(annotationSpec);
    return new PerElementAnnotationDatasetReader<>(annotationDecoder, blobReader);
  }

  private AnnotationDecoder<?> createAnnotationDecoder(ResolvedIntAnnotationSpec annotationSpec) {
    IntReadValueFunction readValueFunction =
        readValueFunctionFactory.createIntReadValueFunction(annotationSpec.storageType());

    return switch (annotationSpec.intEncoding()) {
      case NullableIntEncoding _ -> new NullableIntAnnotationDecoder(readValueFunction);
      case OffsetIntEncoding offsetIntEncoding ->
          new OffsetIntAnnotationDecoder(readValueFunction, offsetIntEncoding.offset());
      case OffsetNullableIntEncoding offsetNullableIntEncoding ->
          new OffsetNullableIntAnnotationDecoder(
              readValueFunction, offsetNullableIntEncoding.offset());
      case PlainIntEncoding _ -> new IntAnnotationDecoder(readValueFunction);
    };
  }
}
