package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;

@RequiredArgsConstructor
public class IntAnnotationDatasetDecoderFactory {
  private final ReadValueFunctionFactory readValueFunctionFactory;

  public AnnotationDatasetDecoder<?> create(
      IntAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    AnnotationDecoder<?> annotationDecoder = createAnnotationDecoder(annotationSpec);
    return new PerElementAnnotationDatasetReader<>(annotationDecoder, blobReader);
  }

  private AnnotationDecoder<?> createAnnotationDecoder(IntAnnotationSpec annotationSpec) {
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
