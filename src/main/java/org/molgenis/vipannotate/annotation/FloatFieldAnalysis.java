package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.def.FloatAnnotationDef;

public record FloatFieldAnalysis(FloatAnnotationDef annotationSpec, FloatAnnotationStats stats)
    implements FieldAnalysis {}
