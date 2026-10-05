package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.def.EnumAnnotationDef;

public record EnumFieldAnalysis(EnumAnnotationDef annotationSpec, EnumAnnotationStats stats)
    implements FieldAnalysis {}
