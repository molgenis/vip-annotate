package org.molgenis.vipannotate.annotation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.def.AnnotationDbDef;
import org.molgenis.vipannotate.annotation.def.AnnotationType;
import org.molgenis.vipannotate.annotation.spec.AnnotationDbSpec;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.AnnotationsSpec;
import org.molgenis.vipannotate.annotation.spec.PositionBinSpec;

@RequiredArgsConstructor
public class AnnotationDbSpecResolver {
  private final AnnotationsSpecResolver annotationsSpecResolver;
  private final PartitioningSpecResolver partitioningSpecResolver;

  public AnnotationDbSpec resolve(AnnotationDbDef annotationDbDef, InputAnalyses inputAnalyses) {
    AnnotationType annotationType = annotationDbDef.annotationsDef().annotationType();
    Map<String, AnnotationSpec> annotationSpecMap =
        annotationsSpecResolver.resolve(inputAnalyses.annotationAnalyses(), annotationType);

    Map<String, PositionBinSpec> partitioningSpec =
        partitioningSpecResolver.resolve(inputAnalyses.contigAnalyses());

    return new AnnotationDbSpec(
        annotationDbDef.version(),
        annotationDbDef.id(),
        annotationDbDef.description(),
        new AnnotationsSpec(
            annotationType, inputAnalyses.sequenceVariantTypes(), annotationSpecMap),
        partitioningSpec);
  }
}
