package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.format.Field;

@RequiredArgsConstructor
public class EnumSetAnnotationAnalyzer implements AnnotationAnalyzer<Field> {
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
  public EnumSetAnnotationAnalysis collect() {
    return new EnumSetAnnotationAnalysis(
        annotationSpec, new EnumSetAnnotationStats(count, nullCount));
  }
}
