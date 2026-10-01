package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.AnnotationSpecs;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface InputAnalyzer extends AutoCloseableNoThrow {
  InputAnalyses analyze(AnnotationSpecs annotationSpecs);
}
