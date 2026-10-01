package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.resolved.ResolvedEnumSetAnnotationSpec;

public class EnumSetAnnotationDatasetDecoderFactory {
  public AnnotationDatasetDecoder<?> create(
      ResolvedEnumSetAnnotationSpec annotationSpec, AnnotationBlobReader blobReader) {
    return new EnumSetAnnotationDatasetDecoder(annotationSpec, blobReader);
  }
}
