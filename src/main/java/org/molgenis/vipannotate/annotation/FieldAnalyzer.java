package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.format.Field;

public interface FieldAnalyzer<T extends Field> {
  void analyze(T field);

  FieldAnalysis collect();
}
