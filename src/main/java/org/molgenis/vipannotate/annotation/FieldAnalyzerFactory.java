package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.Field;

public class FieldAnalyzerFactory<F extends Field> {
  public FieldAnalyzer<F> create(AnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec spec -> new EnumFieldAnalyzer<>(spec);
      case EnumSetAnnotationSpec spec -> new EnumSetFieldAnalyzer<>(spec);
      case FloatAnnotationSpec spec -> new FloatFieldAnalyzer<>(spec);
      case IntAnnotationSpec spec -> new IntFieldAnalyzer<>(spec);
    };
  }
}
