package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.def.EnumSetAnnotationDef;

public record EnumSetFieldAnalysis(
    EnumSetAnnotationDef annotationSpec, EnumSetAnnotationStats stats) implements FieldAnalysis {}
