package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.def.FloatAnnotationDef;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class FloatFieldAnalyzer<F extends Field> implements FieldAnalyzer<F> {
  private final FloatAnnotationDef annotationSpec;

  private long count;
  private long nullCount;
  private double min = Double.MAX_VALUE;
  private double max = Double.MIN_VALUE;

  @Override
  public void analyze(F field) {
    count++;
    if (field.isMissingValue()) {
      nullCount++;
    } else {
      // TODO perf: prevent toString
      CharSequence charSequence = field.getRawView();
      double number = Double.parseDouble(charSequence.toString());
      if (number < min) {
        min = number;
      }
      if (number > max) {
        max = number;
      }
    }
  }

  @Override
  public FloatFieldAnalysis collect() {
    return new FloatFieldAnalysis(
        annotationSpec, new FloatAnnotationStats(count, nullCount, min, max));
  }
}
