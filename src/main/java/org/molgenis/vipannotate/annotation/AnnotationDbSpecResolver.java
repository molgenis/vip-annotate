package org.molgenis.vipannotate.annotation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.def.AnnotationDbDef;
import org.molgenis.vipannotate.annotation.def.AnnotationType;
import org.molgenis.vipannotate.annotation.spec.AnnotationDbSpec;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.AnnotationsSpec;

@RequiredArgsConstructor
public class AnnotationDbSpecResolver {
  private final AnnotationsSpecResolver annotationsSpecResolver;

  public AnnotationDbSpec resolve(AnnotationDbDef annotationDbDef, InputAnalyses inputAnalyses) {
    AnnotationType annotationType = annotationDbDef.annotationsDef().annotationType();
    Map<String, AnnotationSpec> annotationDbSpecMap =
        annotationsSpecResolver.resolve(inputAnalyses.annotationAnalyses(), annotationType);

    return new AnnotationDbSpec(
        annotationDbDef.version(),
        annotationDbDef.id(),
        annotationDbDef.description(),
        new AnnotationsSpec(
            annotationType, inputAnalyses.sequenceVariantTypes(), annotationDbSpecMap));
  }
}
