package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;

public class EnumAnnotationDatasetDecoderFactory {
  public AnnotationDatasetDecoder<?> create(EnumAnnotationSpec annotationSpec) {
    AnnotationDatasetDecoder<?> datasetDecoder = new EnumAnnotationDatasetDecoder(annotationSpec);
    if (annotationSpec.nullable()) {
      datasetDecoder = new NullableAnnotationDatasetDecoder<>(datasetDecoder);
    }
    return datasetDecoder;
  }
}
