package org.molgenis.vipannotate.annotation;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.annotation.spec.*;

@RequiredArgsConstructor
public final class CompositeAnnotationPool extends Pool<CompositeAnnotation> {
  public final Collection<AnnotationSpec> annotationSpecs;

  @Override
  protected CompositeAnnotation create() {
    Annotation[] annotations = new Annotation[annotationSpecs.size()];
    int i = 0;
    for (AnnotationSpec annotationSpec : annotationSpecs) {
      annotations[i++] = create(annotationSpec);
    }
    return new CompositeAnnotation(annotations);
  }

  private Annotation create(AnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec _ -> new StringAnnotation(null);
      case EnumSetAnnotationSpec _ -> new StringListAnnotation(new String[0]);
      case FloatAnnotationSpec spec ->
          switch (spec.floatEncoding()) {
            case NullableFloatEncoding _ -> new NullableFloatAnnotation();
            case PlainFloatEncoding _ -> new FloatAnnotation(0);
            case QuantizedEncoding quantizedEncoding ->
                quantizedEncoding.nullCode() != null
                    ? new NullableFloatAnnotation()
                    : new FloatAnnotation(Float.NaN);
          };
      case IntAnnotationSpec intAnnotationSpec ->
          switch (intAnnotationSpec.intEncoding()) {
            case NullableIntEncoding _ -> new NullableIntAnnotation();
            case OffsetIntEncoding _ -> new IntAnnotation(0);
            case OffsetNullableIntEncoding _ -> new NullableIntAnnotation();
            case PlainIntEncoding _ -> new IntAnnotation(0);
          };
    };
  }
}
