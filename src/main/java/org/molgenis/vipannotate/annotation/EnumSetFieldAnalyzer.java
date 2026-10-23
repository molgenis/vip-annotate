package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.def.EnumSetAnnotationDef;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class EnumSetFieldAnalyzer<F extends Field> implements FieldAnalyzer<F> {
  private final EnumSetAnnotationDef annotationSpec;

  private long count;
  private long nullCount;

  @Override
  public void analyze(F field) {
    count++;
    if (field.isMissingValue()) {
      nullCount++;
    }
  }

  @Override
  public EnumSetFieldAnalysis collect() {
    return new EnumSetFieldAnalysis(annotationSpec, new EnumSetAnnotationStats(count, nullCount));
  }
}
