package org.molgenis.vipannotate.annotation;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
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
      case EnumAnnotationSpec _ -> new StringAnnotation();
      case EnumSetAnnotationSpec _ -> new StringListAnnotation();
      case FloatAnnotationSpec _ -> new FloatAnnotation();
      case IntAnnotationSpec _ -> new IntAnnotation();
    };
  }
}
