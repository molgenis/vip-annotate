package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.IntAnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.NullableIntEncoding;
import org.molgenis.vipannotate.annotation.spec.OffsetIntEncoding;
import org.molgenis.vipannotate.annotation.spec.OffsetNullableIntEncoding;
import org.molgenis.vipannotate.annotation.spec.PlainIntEncoding;

@RequiredArgsConstructor
public class IntAnnotationEncoderFactory {
  private final ValueWriterFactory valueWriterFactory;

  public AnnotationEncoder<?> create(IntAnnotationSpec annotationSpec) {
    IntValueWriter valueWriter =
        valueWriterFactory.createIntValueWriter(annotationSpec.storageType());

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
