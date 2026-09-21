package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.format.Field;

public interface AnnotationAnalyzer<T extends Field> {
  void analyze(T field);

  AnnotationAnalysis collect();
}
