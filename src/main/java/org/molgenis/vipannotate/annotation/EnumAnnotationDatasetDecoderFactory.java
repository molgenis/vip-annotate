package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.resolved.ResolvedEnumAnnotationSpec;

public class EnumAnnotationDatasetDecoderFactory {
  public AnnotationDatasetDecoder<?> create(
      ResolvedEnumAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    return new EnumAnnotationDatasetReader(annotationSpec, blobReader);
  }
}
