package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;

public class EnumSetAnnotationDatasetDecoderFactory {
  public AnnotationDatasetDecoder<?> create(
      EnumSetAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    return new EnumSetAnnotationDatasetDecoder(annotationSpec, blobReader);
  }
}
