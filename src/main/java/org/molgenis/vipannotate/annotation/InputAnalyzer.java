package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.AnnotationSpecs;
import org.molgenis.vipannotate.util.Input;

public interface InputAnalyzer {
  InputAnalyses analyze(Input input, AnnotationSpecs annotationSpecs);
}
