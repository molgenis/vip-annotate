package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;

public class EnumAnnotationDatasetDecoderFactory {
  public AnnotationDatasetDecoder<?> create(
      EnumAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    return new EnumAnnotationDatasetReader(annotationSpec, blobReader);
  }
}
