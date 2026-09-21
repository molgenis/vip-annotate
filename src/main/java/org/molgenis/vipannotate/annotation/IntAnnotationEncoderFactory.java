package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.NullableIntEncoding;
import org.molgenis.vipannotate.annotation.resolved.OffsetIntEncoding;
import org.molgenis.vipannotate.annotation.resolved.OffsetNullableIntEncoding;
import org.molgenis.vipannotate.annotation.resolved.PlainIntEncoding;
import org.molgenis.vipannotate.annotation.resolved.ResolvedIntAnnotationSpec;

@RequiredArgsConstructor
public class IntAnnotationEncoderFactory {
  private final ValueWriterFactory valueWriterFactory;

  public AnnotationEncoder<?> create(ResolvedIntAnnotationSpec annotationSpec) {
    return create(annotationSpec, false);
  }

  public AnnotationEncoder<?> createIndexed(ResolvedIntAnnotationSpec annotationSpec) {
    return create(annotationSpec, true);
  }

  private AnnotationEncoder<?> create(ResolvedIntAnnotationSpec annotationSpec, boolean indexed) {
    IntValueWriter valueWriter =
        indexed
            ? valueWriterFactory.createIndexedIntValueWriter(annotationSpec.storageType())
            : valueWriterFactory.createIntValueWriter(annotationSpec.storageType());

    return switch (annotationSpec.intEncoding()) {
      case NullableIntEncoding _ -> new NullableIntAnnotationEncoder(valueWriter);
      case OffsetIntEncoding offsetIntEncoding ->
          new OffsetIntAnnotationEncoder(valueWriter, offsetIntEncoding.offset());
      case OffsetNullableIntEncoding offsetNullableIntEncoding ->
          new OffsetNullableIntAnnotationEncoder(valueWriter, offsetNullableIntEncoding.offset());
      case PlainIntEncoding _ -> new IntAnnotationEncoder(valueWriter);
    };
  }
}
