package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class EnumSetFieldAnalyzer implements FieldAnalyzer<Field> {
  private final EnumSetAnnotationSpec annotationSpec;

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
  public EnumSetFieldAnalysis collect() {
    return new EnumSetFieldAnalysis(annotationSpec, new EnumSetAnnotationStats(count, nullCount));
  }
}
