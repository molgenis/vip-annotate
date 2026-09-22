package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class EnumFieldAnalyzer implements FieldAnalyzer<Field> {
  private final EnumAnnotationSpec annotationSpec;

  private long count;
  private long nullCount;

  @Override
  public void analyze(Field field) {
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
