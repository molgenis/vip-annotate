package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class EnumFieldAnalyzer<F extends Field> implements FieldAnalyzer<F> {
  private final EnumAnnotationSpec annotationSpec;

  private long count;
  private long nullCount;

  @Override
  public void analyze(F field) {
    CharSequence charSequence = field.getRawView();

    count++;
    if (charSequence.isEmpty()) {
      nullCount++;
    }
  }

  @Override
  public EnumFieldAnalysis collect() {
    return new EnumFieldAnalysis(annotationSpec, new EnumAnnotationStats(count, nullCount));
  }
}
