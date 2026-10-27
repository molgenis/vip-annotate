package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;

public class EnumSetAnnotationDatasetDecoderFactory {
  public AnnotationDatasetDecoder<?> create(EnumSetAnnotationSpec annotationSpec) {
    AnnotationDatasetDecoder<?> datasetDecoder =
        new EnumSetAnnotationDatasetDecoder(annotationSpec);
    if (annotationSpec.nullable()) {
      datasetDecoder = new NullableAnnotationDatasetDecoder<>(datasetDecoder);
    }
    return datasetDecoder;
  }
}
