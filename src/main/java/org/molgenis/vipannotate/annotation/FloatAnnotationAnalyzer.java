package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.FloatAnnotationSpec;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class FloatAnnotationAnalyzer implements AnnotationAnalyzer<Field> {
  private final FloatAnnotationSpec annotationSpec;

  private long count;
  private long nullCount;
  private double min = Double.MAX_VALUE;
  private double max = Double.MIN_VALUE;

  @Override
  public void analyze(Field field) {
    CharSequence charSequence = field.getRawView();

    count++;
    if (charSequence.isEmpty()) {
      nullCount++;
    } else {
      // TODO perf: prevent toString
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
  public FloatAnnotationAnalysis collect() {
    return new FloatAnnotationAnalysis(
        annotationSpec, new FloatAnnotationStats(count, nullCount, min, max));
  }
}
