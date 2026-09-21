package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.AnnotationSpecs;
import org.molgenis.vipannotate.util.Input;

public interface AnnotationsAnalyzer {
  AnnotationAnalyses analyze(Input input, AnnotationSpecs annotationSpecs);
}
