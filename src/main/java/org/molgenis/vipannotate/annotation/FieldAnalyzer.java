package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.format.Field;

public interface FieldAnalyzer<F extends Field> {
  void analyze(F field);

  FieldAnalysis collect();
}
