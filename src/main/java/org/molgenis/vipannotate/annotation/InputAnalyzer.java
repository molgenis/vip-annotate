package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.def.AnnotationsDef;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface InputAnalyzer extends AutoCloseableNoThrow {
  InputAnalyses analyze(AnnotationsDef annotationsDef);
}
