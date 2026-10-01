package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.def.*;
import org.molgenis.vipannotate.format.Field;

public class FieldAnalyzerFactory<F extends Field> {
  public FieldAnalyzer<F> create(AnnotationDef annotationDef) {
    return switch (annotationDef) {
      case EnumAnnotationDef spec -> new EnumFieldAnalyzer<>(spec);
      case EnumSetAnnotationDef spec -> new EnumSetFieldAnalyzer<>(spec);
      case FloatAnnotationDef spec -> new FloatFieldAnalyzer<>(spec);
      case IntAnnotationDef spec -> new IntFieldAnalyzer<>(spec);
    };
  }
}
