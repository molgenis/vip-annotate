package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationDbSpec;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSchema;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.AnnotationDbSpec;
import org.molgenis.vipannotate.annotation.spec.AnnotationType;

@RequiredArgsConstructor
public class AnnotationDbSpecResolver {
  private final AnnotationSpecResolver annotationSpecResolver;

  public ResolvedAnnotationDbSpec resolve(
      AnnotationDbSpec annotationDbSpec, InputAnalyses inputAnalyses) {

    AnnotationType annotationType = annotationDbSpec.annotationSchema().annotationType();
    ResolvedAnnotationSpecs annotationSpecs =
        annotationSpecResolver.resolve(inputAnalyses.annotationAnalyses(), annotationType);

    return new ResolvedAnnotationDbSpec(
        annotationDbSpec.specVersion(),
        annotationDbSpec.specId(),
        annotationDbSpec.specDescription(),
        new ResolvedAnnotationSchema(
            annotationType, inputAnalyses.sequenceVariantTypes(), annotationSpecs));
  }
}
