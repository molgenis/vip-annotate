package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.IntAnnotationSpec;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class IntFieldAnalyzer<F extends Field> implements FieldAnalyzer<F> {
  private final IntAnnotationSpec annotationSpec;

  private long count;
  private long nullCount;
  private long min = Long.MAX_VALUE;
  private long max = Long.MIN_VALUE;

  @Override
  public void analyze(F field) {
    count++;
    if (field.isMissingValue()) {
      nullCount++;
    } else {
      CharSequence charSequence = field.getRawView();
      long number = Long.parseLong(charSequence, 0, charSequence.length(), 10);
      if (number < min) {
        min = number;
      }
      if (number > max) {
        max = number;
      }
    }
  }

  @Override
  public IntFieldAnalysis collect() {
    return new IntFieldAnalysis(annotationSpec, new IntAnnotationStats(count, nullCount, min, max));
  }
}
